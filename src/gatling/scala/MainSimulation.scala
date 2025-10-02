import Config._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory


class MainSimulation extends Simulation {
  private val logger = LoggerFactory.getLogger(getClass.getSimpleName)
  private val httpProtocol = http.baseUrl(baseUrl)

  val loginSteps: ChainBuilder = exec(
      exec(http("GoToLoginPage").get("/login")
        .check(status.is(200))
        .check(css("title").is("Sign in to National Delius"))
        .check(css("input[name=_csrf]", "value").saveAs("csrf"))
      ),
      feed(Data.usernames).exec(http("SubmitCredentials").get("/oauth2/authorize")
        .basicAuth("${username}", Config.password)
        .queryParam("response_type", "code")
        .queryParam("client_id", "UserManagement-UI")
        .queryParam("redirect_uri", "/umt/")
        .queryParam("scope", "UMBI001 UMBI002 UMBI003 UMBI004 UMBI005 UMBI006 UMBI007 UMBI008 UMBI009 UMBI010 UMBI011 UMBI012 UABT0050 UABI020 UABI021 UABI022 UABI023 UABI024 UABI025 UABI026")
        .queryParam("resource", "NDelius-UI")
        .check(status.is(200))
        .check(currentLocationRegex(".*code=([^&]*).*").saveAs("authorization_code"))
      ),
      exec(http("GetAccessToken").post("/oauth2/token")
        .basicAuth("UserManagement-UI", "")
        .queryParam("grant_type", "authorization_code")
        .queryParam("code", "${authorization_code}")
        .queryParam("redirect_uri", "/umt/")
        .check(status.is(200))
        .check(jmesPath("access_token").saveAs("access_token"))
      ),
      exec(http("GetCurrentUserDetails").get("/api/whoami")
        .header("Authorization", "Bearer ${access_token}")
        .check(status.is(200))
        .check(jmesPath("username").is("${username}"))
        .resources(
          http("Datasets").get("/api/datasets")
            .header("Authorization", "Bearer ${access_token}")
            .check(status.is(200)),
          http("ReportingGroups").get("/api/groups/NDMIS-Reporting")
            .header("Authorization", "Bearer ${access_token}")
            .check(status.is(200)),
          http("FileshareGroups").get("/api/groups/Fileshare")
            .header("Authorization", "Bearer ${access_token}")
            .check(status.is(200)),
          http("Roles").get("/api/roles")
            .header("Authorization", "Bearer ${access_token}")
            .check(status.is(200))
        )
      )
    )

  val scenarios = List(
    new Scenario {
      override val name: String = "Search"
      override val transactionsPerHour: Int = 44
      override val steps: ChainBuilder = feed(Data.usernames)
        .exec(http("SearchByUsername").get("/api/users")
          .header("Authorization", "Bearer ${access_token}")
          .queryParam("q", "${username}")
          .queryParam("includeInactiveUsers", "false")
          .queryParam("page", "1")
          .queryParam("pageSize", "50")
          .check(status.is(200))
          .check(jmesPath("[0].username").is("${username}")))
    },

    new Scenario {
      override val name: String = "ViewUser"
      override val transactionsPerHour: Int = 53
      override val steps: ChainBuilder = feed(Data.usernames).exec(
        exec(http("FetchUserDetails").get("/api/user/${username}")
          .header("Authorization", "Bearer ${access_token}")
          .check(status.is(200))
          .check(jmesPath("username").is("${username}"))
          .resources(
            http("FetchUserHistory").get("/api/user/${username}/history")
              .header("Authorization", "Bearer ${access_token}")
              .check(status.is(200)),
            http("RoleGroups").get("/api/rolegroups")
              .header("Authorization", "Bearer ${access_token}")
              .check(status.is(200)),
            http("Roles").get("/api/roles")
              .header("Authorization", "Bearer ${access_token}")
              .check(status.is(200)),
            http("Groups").get("/api/groups")
              .header("Authorization", "Bearer ${access_token}")
              .check(status.is(200)),
            http("Establishments").get("/api/establishments")
              .header("Authorization", "Bearer ${access_token}")
              .check(status.is(200)),
            http("StaffGrades").get("/api/staffgrades")
              .header("Authorization", "Bearer ${access_token}")
              .check(status.is(200)),
            http("Datasets").get("/api/datasets")
              .header("Authorization", "Bearer ${access_token}")
              .check(status.is(200))
              .check(jsonPath("$[*].code").findRandom.saveAs("homearea"))
              .resources(
                http("SubContractedProviders").get("/api/dataset/${homearea}/subContractedProviders")
                  .header("Authorization", "Bearer ${access_token}")
                  .check(status.is(200)),
                http("Teams").get("/api/teams")
                  .header("Authorization", "Bearer ${access_token}")
                  .queryParam("provider", "${homearea}")
                  .check(status.is(200))
              )
          )
        )
      )
    },

    new Scenario {
      override val name: String = "UpdateUser"
      override val transactionsPerHour: Int = 5
      override val steps: ChainBuilder = feed(Data.usernames).exec(
        exec(http("FetchUserDetails").get("/api/user/${username}")
          .header("Authorization", "Bearer ${access_token}")
          .check(status.is(200))
          .check(jmesPath("username").is("${username}"))
          .check(bodyString.saveAs("body"))
        ),
        exec(http("UpdateUser").post("/api/user/${username}")
          .header("Authorization", "Bearer ${access_token}")
          .header("Content-Type", "application/json")
          .body(StringBody("${body}"))
          .check(status.is(204))
        )
      )
    }
  )

  val weightsSum: Int = scenarios.map(i => i.transactionsPerHour).sum

  if (duration == 0) {
    setUp(scenarios.map(scn => scenario(scn.name)
      // Run each scenario once (for testing)
      .exec(exitBlockOnFail(exec(
        group("Login") { loginSteps },
        group(scn.name) { scn.steps }
      )))
      .inject(atOnceUsers(Math.max(1, concurrentUsers * scn.transactionsPerHour / weightsSum)))))
      .protocols(httpProtocol)
  } else {
    setUp(scenarios.map(scn => {
      val weightedUsers = Math.max(1, concurrentUsers * scn.transactionsPerHour / weightsSum)
      val scale = concurrentUsers / observedConcurrentUsers
      val transactionsPerHour = scale * scn.transactionsPerHour
      val secondsBetweenTransactions = 3600 / transactionsPerHour
      logger.info(s"Building ${scn.name} scenario, " +
        s"with a frequency of ${transactionsPerHour} transactions/hour " +
        s"and ${weightedUsers} virtual users, resulting in an average of ${secondsBetweenTransactions} seconds between each transaction. " +
        s"Estimated transaction count during this test = ${duration.toFloat / secondsBetweenTransactions.toFloat}.")
      scenario(scn.name).exec(
        // Run each scenario repeatedly for the duration
        during(duration - rampUpDuration) {
          exitBlockOnFail(exec(
            pause(0, weightedUsers * secondsBetweenTransactions),
            group("Login") { loginSteps },
            forever(group(scn.name) {
              pace(weightedUsers * secondsBetweenTransactions).exec(scn.steps)
            })
          ))
        })
        .inject(rampUsers(weightedUsers) during rampUpDuration)
    }))
      .protocols(httpProtocol)
      .maxDuration(duration)
  }
}

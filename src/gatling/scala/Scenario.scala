import io.gatling.core.structure.ChainBuilder

trait Scenario {
  val name: String
  val steps: ChainBuilder
  val transactionsPerHour: Int /* Average requests-per-hour in Production while under load.
                                *
                                * This value can be fetched from the User Management audit logs in CloudWatch Log Insights, by
                                * querying on interaction code e.g.
                                * > filter @logStream like /^ecs\/usermanagement/ and @message like /UMBI001/ | stats count(*) by bin(1h)
                                */
}

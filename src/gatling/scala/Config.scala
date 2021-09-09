object Config {
  val baseUrl: String = sys.env("BASE_URL")
  val username: String = sys.env.getOrElse("TEST_USERNAME", null)
  val password: String = sys.env("TEST_PASSWORD")
  val concurrentUsers: Int = sys.env.getOrElse("CONCURRENT_USERS", "50").toInt
  val duration: Int = sys.env.getOrElse("DURATION", "0").toInt // Set to 0 to run once
  val rampUpDuration: Int = sys.env.getOrElse("RAMP_UP_DURATION", (duration / 8).toString).toInt


  // Get the following values from CloudWatch logs/metrics in Production:
  val observedConcurrentUsers: Int = 50
  val observedRequestsPerMinute: Int = 15

  val actualRequestsPerMinute: Float = observedRequestsPerMinute * concurrentUsers / observedConcurrentUsers
  val actualRequestsPerSecond: Float = actualRequestsPerMinute / 60
}

import java.util.concurrent.ConcurrentLinkedQueue
import scala.util.Random

object Data {
  val usernames: Iterator[Map[String, String]] =
    if (Config.username != null) {
      // Use a single username if one is configured
      Iterator.continually(Map("username" -> Config.username))
    } else {
      // Otherwise use a random username of the format nd.perf09999
      Iterator.continually(Map("username" -> "nd.perf0%04d".format(Random.nextInt(10000))))
    }

  val userResponseBodyQueue = new ConcurrentLinkedQueue[Map[String, String]]()
  val userResponseBody: Iterator[Map[String, String]] = Iterator.continually(userResponseBodyQueue.poll())
}

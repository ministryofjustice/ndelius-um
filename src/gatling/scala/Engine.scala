import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

object Engine extends App {
  Gatling.fromMap(new GatlingPropertiesBuilder()
    .simulationClass(classOf[MainSimulation].getName)
    .build)
}

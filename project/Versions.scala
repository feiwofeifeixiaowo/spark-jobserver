object Versions {
  val jdkVersion = scala.util.Properties.isJavaAtLeast("1.8")
  // CDH 5.3/5.4 comes with akka 2.2.3-shaded-protobuf, which is built against com.typesafe.config-1.0.2,
  // hence we need to downgrade here
  // USED TO BE: 1.3.0/1.2.1 (depending on jdk version)
  lazy val typeSafeConfig = "1.0.2"
  lazy val metrics = "2.2.0"
  lazy val jodaTime = "2.9.3"
  lazy val jodaConvert = "1.8.1"
   // CDH 5.3/5.4 comes with akka 2.2.3-shaded-protobuf preinstalled, so we should build against that version.
  lazy val akka = "2.2.3-shaded-protobuf"
  lazy val akkaCluster = "2.2.3"
  // io.spray 1.3.3 pulls in a newer akka version than the one included in CDH 5.3. We neeed to downgrade
  // io.spray to 1.2.3 so we can use the akka version included in CDH 5.3.
  // USED TO BE: 1.3.3
  lazy val spray = "1.2.3"
  lazy val sprayJson = "1.2.3"
  lazy val spark = "1.5.0-cdh5.5.4"
  lazy val mesos = "0.25.0-0.2.70.ubuntu1404"
  lazy val netty =  "4.0.29.Final"
  lazy val slick = "3.1.1"
  lazy val h2 = "1.3.176"
  lazy val commons = "1.4"
  lazy val flyway = "3.2.1"
  lazy val logback = "1.0.7"
  lazy val scalaTest = "2.2.6"
  lazy val scalatic = "2.2.6"
  lazy val shiro = "1.2.4"
}
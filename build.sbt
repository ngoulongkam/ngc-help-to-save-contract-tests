name := "ngc-help-to-save-contract-tests"

scalaVersion := "2.11.12"

fork in Test := false
parallelExecution in Test := false
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports/html-report")
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/test-reports")
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF")

resolvers += Resolver.bintrayRepo("hmrc", "releases")

val playVersion = "2.5.18"
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "io.lemonlabs" %% "scala-uri" % "1.1.1" % "test",
  "org.typelevel" %% "cats-core" % "1.0.1",
  "org.pegdown" % "pegdown" % "1.6.0" % "test",
  "com.typesafe.play" %% "play-test" % playVersion % "test",
  "com.typesafe.play" %% "play-ws" % playVersion % "test",
  "uk.gov.hmrc" %% "domain" % "5.1.0" % "test",
  "uk.gov.hmrc" %% "http-verbs" % "7.2.0" % "test",
  "uk.gov.hmrc" %% "http-verbs-play-25" % "0.9.0" % "test"
)

import sbtversionpolicy.Compatibility.BinaryCompatible

name := "serially"

organization := "com.evolutiongaming"

homepage := Some(uri("https://github.com/evolution-gaming/serially"))

startYear := Some(2018)

organizationName := "Evolution"

organizationHomepage := Some(uri("https://evolution.com"))

scalaVersion := crossScalaVersions.value.head

crossScalaVersions := Seq("2.13.18", "3.3.8")

scalacOptions ++= crossSettings(
  scalaVersion = scalaVersion.value,
  // Good compiler options for Scala 2.13 are coming from com.evolution:sbt-scalac-opts-plugin:0.1.0,
  // but its support for Scala 3 is limited, especially what concerns linting options.
  //
  // If Scala 3 is made the primary target, good linting scalac options for it should be added first.
  if3 = Seq(
    "-Ykind-projector:underscores",

    // disable new brace-less syntax:
    // https://alexn.org/blog/2022/10/24/scala-3-optional-braces/
    "-no-indent",

    // improve error messages:
    "-explain",
    "-explain-types",
  ),
  if2 = Seq(
    "-Xsource:3",
  ),
)

Compile / doc / scalacOptions ++= Seq("-groups", "-implicits", "-no-link-warnings")

publishTo := Some(Resolver.evolutionReleases)

versionPolicyIntention := BinaryCompatible

libraryDependencies ++= Seq(
  "com.evolutiongaming" %% "future-helper" % "1.0.7",
  "com.typesafe.akka" %% "akka-actor" % "2.6.21", // scala-steward:off
  "com.typesafe.akka" %% "akka-testkit" % "2.6.21" % Test, // scala-steward:off
  "org.scalatest" %% "scalatest" % "3.2.20" % Test,
)

licenses := Seq(("MIT", uri("https://opensource.org/licenses/MIT")))

def crossSettings[T](scalaVersion: String, if3: T, if2: T): T = {
  scalaVersion match {
    case version if version.startsWith("3") => if3
    case _ => if2
  }
}

addCommandAlias("check", "+all scalafmtCheckRepo versionPolicyCheck Compile/doc")
addCommandAlias("fmt", "+all scalafmtRepo")
addCommandAlias("build", "+all compile testFull")

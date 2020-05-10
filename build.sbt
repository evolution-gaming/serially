name := "serially"

organization := "com.evolutiongaming"

homepage := Some(new URL("http://github.com/evolution-gaming/serially"))

startYear := Some(2018)

organizationName := "Evolution Gaming"

organizationHomepage := Some(url("http://evolutiongaming.com"))

bintrayOrganization := Some("evolutiongaming")

scalaVersion := crossScalaVersions.value.head

crossScalaVersions := Seq("2.13.1", "2.12.10")

scalacOptions in (Compile,doc) ++= Seq("-groups", "-implicits", "-no-link-warnings")

resolvers += Resolver.bintrayRepo("evolutiongaming", "maven")

libraryDependencies ++= Seq(
  "com.evolutiongaming" %% "future-helper" % "1.0.6",
  "com.typesafe.akka"   %% "akka-actor"    % "2.6.4",
  "com.typesafe.akka"   %% "akka-testkit"  % "2.6.4" % Test,
  "org.scalatest"       %% "scalatest"     % "3.1.2"  % Test)

licenses := Seq(("MIT", url("https://opensource.org/licenses/MIT")))

releaseCrossBuild := true
name := "serially"

organization := "com.evolutiongaming"

homepage := Some(url("https://github.com/evolution-gaming/serially"))

startYear := Some(2018)

organizationName := "Evolution"

organizationHomepage := Some(url("https://evolution.com"))

scalaVersion := crossScalaVersions.value.head

crossScalaVersions := Seq("2.13.5", "2.12.13")

Compile / doc / scalacOptions ++= Seq("-groups", "-implicits", "-no-link-warnings")

publishTo := Some(Resolver.evolutionReleases)

libraryDependencies ++= Seq(
  "com.evolutiongaming" %% "future-helper" % "1.0.6",
  "com.typesafe.akka"   %% "akka-actor"    % "2.6.8",
  "com.typesafe.akka"   %% "akka-testkit"  % "2.6.8" % Test,
  "org.scalatest"       %% "scalatest"     % "3.2.9"  % Test)

licenses := Seq(("MIT", url("https://opensource.org/licenses/MIT")))

releaseCrossBuild := true

//addCommandAlias("check", "all versionPolicyCheck Compile/doc")
addCommandAlias("check", "show version")
addCommandAlias("build", "+all compile test")

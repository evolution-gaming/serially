import sbtversionpolicy.Compatibility.BinaryCompatible

name := "serially"

organization := "com.evolutiongaming"

homepage := Some(uri("https://github.com/evolution-gaming/serially"))

startYear := Some(2018)

organizationName := "Evolution"

organizationHomepage := Some(uri("https://evolution.com"))

scalaVersion := crossScalaVersions.value.head

crossScalaVersions := Seq("2.13.5", "2.12.13")

Compile / doc / scalacOptions ++= Seq("-groups", "-implicits", "-no-link-warnings")

publishTo := Some(Resolver.evolutionReleases)

versionPolicyIntention := BinaryCompatible

libraryDependencies ++= Seq(
  "com.evolutiongaming" %% "future-helper" % "1.0.6",
  "com.typesafe.akka"   %% "akka-actor"    % "2.6.8",
  "com.typesafe.akka"   %% "akka-testkit"  % "2.6.8" % Test,
  "org.scalatest"       %% "scalatest"     % "3.2.9"  % Test)

licenses := Seq(("MIT", uri("https://opensource.org/licenses/MIT")))

addCommandAlias("check", "all versionPolicyCheck Compile/doc")
addCommandAlias("build", "+all compile testFull")

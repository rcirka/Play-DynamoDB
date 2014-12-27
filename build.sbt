name := "play-dynamodb"

organization := "com.rcirka"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.3.0" % "provided" cross CrossVersion.binary,
  "com.typesafe.play" %% "play-ws" % "2.3.0" % "provided" cross CrossVersion.binary,
  "com.typesafe.play" %% "play-test" % "2.3.0" % "provided" cross CrossVersion.binary,
  "com.amazonaws" % "aws-java-sdk-core" % "1.9.12",
  "org.specs2" %% "specs2" % "2.4.15"
)

scalacOptions in Test ++= Seq("-Yrangepos")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

parallelExecution in Test := false

testOptions in Test += Tests.Argument("-showtimes", "1")
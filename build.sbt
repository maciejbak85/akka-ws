name := "akka-ws"

version := "1.0"

scalaVersion := "2.12.3"

val akkaVersion = "2.5.3"
val akkaHttpVersion = "10.0.9"

val akkaHttpDeps = Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
)

val akkaDeps = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
)

val akkaStreamsDeps = Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
)

libraryDependencies ++= akkaDeps ++ akkaHttpDeps ++ akkaStreamsDeps

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
)
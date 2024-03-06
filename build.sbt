ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

//enablePlugins(FlywayPlugin)

lazy val root = (project in file("."))
  .settings(
    name := "MobileManagementSystem"
  )

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

val AkkaVersion = "2.9.0"
val AkkaHttpVersion = "10.6.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
)

libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.6.0"
libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc"       % "4.2.1",
  "com.h2database"  %  "h2"                % "2.2.224",
  "ch.qos.logback"  %  "logback-classic"   % "1.4.14"
)

// https://mvnrepository.com/artifact/org.postgresql/postgresql
libraryDependencies += "org.postgresql" % "postgresql" % "42.7.1"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.6.0"

// https://mvnrepository.com/artifact/org.flywaydb/flyway-core
libraryDependencies += "org.flywaydb" % "flyway-core" % "8.5.6"

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name                                     := "fs2-kafka-memory-leak",
    libraryDependencies += "com.github.fd4s" %% "fs2-kafka"       % "3.4.0",
    libraryDependencies += "ch.qos.logback"   % "logback-classic" % "1.5.4",
    run / fork := true,
  )

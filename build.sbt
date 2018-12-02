lazy val root = (project in file("."))
  .settings(
    organization := "com.github.rthoth",
    scalaVersion in ThisBuild := "2.12.7",
    name := "akkautil",
    isSnapshot := true,
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.5.18" % Provided,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0" % Provided
    )
  )

val common = Seq(
  organization := "com.github.rthoth",
  scalaVersion in ThisBuild := "2.12.5",
  isSnapshot := true
)


lazy val extensions = project
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.5.11" % Provided,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0" % Provided
    ),
    common
  )

lazy val docker = project
  .settings(
    common
  )

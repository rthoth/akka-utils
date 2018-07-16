val common = Seq(
  organization := "com.github.rthoth",
  scalaVersion in ThisBuild := "2.12.5",
  isSnapshot := true,
  version := "0.1.0-SNAPSHOT"
)


lazy val extensions = project
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.5.11" % Provided,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0" % Provided
    ),
    common
  )

lazy val launcher = project
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.5.11",
      "org.backuity.clist" %% "clist-core"   % "3.4.0" % Provided,
      "org.backuity.clist" %% "clist-macros" % "3.4.0" % Provided,
    ),
    common
  )

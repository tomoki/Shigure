libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"

libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
libraryDependencies += "org.scala-lang" % "scala-library"  % scalaVersion.value
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.60-R9"

scalaVersion := "2.11.7"
name := "Shigure"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-value-discard"
)

// Fork a new JVM for 'run' and 'test:run', to avoid JavaFX double initialization problems
fork := true

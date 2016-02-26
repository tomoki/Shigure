name := "Shigure"
val common_settings = Seq(
  libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
  libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  libraryDependencies += "org.scala-lang" % "scala-library"  % scalaVersion.value,
  libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.60-R9",
  libraryDependencies += "com.lihaoyi" %% "sourcecode" % "0.1.0",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlint",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused",
    "-Ywarn-value-discard"
  ),
  fork := true // Fork a new JVM for 'run' and 'test:run', to avoid JavaFX double initialization problems
)

val elrpc = uri("git://github.com/tomoki/Scala-elrpc.git")

lazy val general = project.in(file("general"))
  .settings(common_settings:_*)
lazy val beamer = project.in(file("beamer"))
  .settings(common_settings:_*).dependsOn(general)

lazy val main = project.in(file("."))
  .settings(common_settings:_*).dependsOn(general, beamer, elrpc)
lazy val testing = project.in(file("testing"))
  .settings(common_settings:_*).dependsOn(general, beamer, elrpc)

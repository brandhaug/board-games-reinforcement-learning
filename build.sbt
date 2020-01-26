// https://github.com/scalafx/ScalaFX-Tutorials/blob/master/scalafxml-example/build.sbt
name := "rl-peg-solitaire"
version := "0.1"

scalaVersion := "2.12.10"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-Xcheckinit", "-encoding", "utf8")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
resourceDirectory in Compile := (scalaSource in Compile).value
libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx"             % "12.0.2-R18",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.5"
)

// Add OS specific JavaFX dependencies
val javafxModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux") => "linux"
  case n if n.startsWith("Mac") => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}
libraryDependencies ++= javafxModules.map(m => "org.openjfx" % s"javafx-$m" % "12.0.2" classifier osName)


// Fork a new JVM for 'run' and 'test:run', to avoid JavaFX double initialization problems
fork := true

shellPrompt := { _ => System.getProperty("user.name") + s":${name.value}> " }

libraryDependencies += "org.platanios" %% "tensorflow" % "0.4.1" classifier f"linux-gpu-x86_64"
libraryDependencies += "org.platanios" %% "tensorflow" % "0.4.1"
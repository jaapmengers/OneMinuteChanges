import com.lihaoyi.workbench.Plugin._

enablePlugins(ScalaJSPlugin)

workbenchSettings

name := "OneMinuteChanges"
version := "0.1-SNAPSHOT"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.8.0",
  "org.monifu" %%% "monifu" % "1.0-M1",
  "com.github.japgolly.scalajs-react" %%% "core" % "0.8.2"
)

jsDependencies ++= Seq(
  "org.webjars" % "react" % "0.12.2" / "react-with-addons.js" commonJSName "React"
)

bootSnippet := "oneminutechanges.Main().main();"


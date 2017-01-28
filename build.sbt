import de.heikoseeberger.sbtheader.CommentStyleMapping
import de.heikoseeberger.sbtheader.license.MIT

lazy val artifactSettings = Seq(
  name := "sbt-spark",
  organization := "com.github.alonsodomin",
  description := "SBT plugin to start writing Spark apps quickly"
)

lazy val pluginSettings = Seq(
  sbtPlugin := true,
  scalaVersion in ThisBuild := "2.10.6"
)

lazy val pluginTestSettings = ScriptedPlugin.scriptedSettings ++ Seq(
  scriptedLaunchOpts ++= Seq(
    "-Xmx1024M",
    "-XX:MaxPermSize=256M",
    "-Dplugin.version=" + version.value,
    "-Dsbttest.base=" + (sourceDirectory.value / "sbt-test").getAbsolutePath
  ),
  scriptedBufferLog := false
)

lazy val allSettings = artifactSettings ++ pluginSettings ++ pluginTestSettings

lazy val `sbt-spark` = (project in file("."))
  .settings(allSettings)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(
    moduleName := "sbt-spark",
    headers := CommentStyleMapping.createFrom(MIT, "2017", "Antonio Alonso Dominguez")
  )
  .settings(addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3"))

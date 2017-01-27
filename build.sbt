
lazy val artifactSettings = Seq(
  name := "sbt-spark",
  organization := "com.github.alonsodomin"
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
  .settings(moduleName := "sbt-spark")
  .settings(addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3"))

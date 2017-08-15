import com.typesafe.sbt.pgp.PgpKeys

lazy val artifactSettings = Seq(
  name := "sbt-spark",
  startYear := Some(2017),
  organization := "com.github.alonsodomin",
  organizationName := "A. Alonso Dominguez",
  description := "SBT plugin to start writing Spark apps quickly",
  licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
  scmInfo := Some(ScmInfo(url("https://github.com/alonsodomin/sbt-spark"), "scm:git:git@github.com:alonsodomin/sbt-spark.git"))
)

lazy val pluginSettings = Seq(
  sbtPlugin := true,
  crossSbtVersions := Seq("0.13.16", "1.0.0")
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

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishTo := Some(
    if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
    else Opts.resolver.sonatypeStaging
  ),
  pomExtra :=
    <url>https://github.com/alonsodomin/sbt-spark</url>
    <developers>
      <developer>
        <id>alonsodomin</id>
        <name>Antonio Alonso Dominguez</name>
        <url>https://github.com/alonsodomin</url>
      </developer>
    </developers>
)

lazy val releaseSettings = {
  import ReleaseTransformations._

  Seq(
    releaseCrossBuild := true,
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      releaseStepCommandAndRemaining("^ scripted"),
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("^ publishSigned"),
      setNextVersion,
      commitNextVersion,
      releaseStepCommand("sonatypeReleaseAll"),
      pushChanges
    )
  )
}

lazy val allSettings = artifactSettings ++
  pluginSettings ++
  pluginTestSettings ++
  publishSettings ++
  releaseSettings

lazy val `sbt-spark` = (project in file("."))
  .settings(allSettings)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(
    moduleName := "sbt-spark"
  )
  .settings(
    //addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")
    libraryDependencies += {
      val currentSbtVersion = (sbtBinaryVersion in pluginCrossBuild).value
      Defaults.sbtPluginExtra("com.eed3si9n" % "sbt-assembly" % "0.14.5", currentSbtVersion, scalaBinaryVersion.value)
    }
  )

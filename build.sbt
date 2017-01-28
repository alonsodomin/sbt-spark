import com.typesafe.sbt.pgp.PgpKeys
import de.heikoseeberger.sbtheader.CommentStyleMapping
import de.heikoseeberger.sbtheader.license.MIT

lazy val artifactSettings = Seq(
  name := "sbt-spark",
  organization := "com.github.alonsodomin",
  description := "SBT plugin to start writing Spark apps quickly",
  licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
  scmInfo := Some(ScmInfo(url("https://github.com/alonsodomin/sbt-spark"), "scm:git:git@github.com:alonsodomin/sbt-spark.git"))
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

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishArtifact in (Compile, packageDoc) := false,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
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

  val sonatypeReleaseAll = ReleaseStep(action = Command.process("sonatypeReleaseAll", _))

  Seq(
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      commitNextVersion,
      sonatypeReleaseAll,
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
    moduleName := "sbt-spark",
    headers := CommentStyleMapping.createFrom(MIT, "2017", "Antonio Alonso Dominguez")
  )
  .settings(addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3"))

/*
 * Copyright (c) 2017 A. Alonso Dominguez
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package sbtspark

import sbt._
import sbtassembly.AssemblyPlugin

object SparkPlugin extends AutoPlugin {
  private final val SparkOrganization = "org.apache.spark"
  private final val SparkPrefix = "spark-"
  private final val SparkValidateSuggestionMsg =
    "The recommended way of configuring Spark dependencies is by using the 'sparkComponents' key, " ++
     "since that ensures that the right scope and version is chosen for you; like in the following example:"

  object autoImport extends SparkKeys

  import Keys._
  import AssemblyPlugin.autoImport._
  import autoImport._

  private[this] val defaultSparkComponentScope: Map[String, Configuration] = Map(
    "core"      -> Provided,
    "sql"       -> Provided,
    "hive"      -> Provided,
    "streaming" -> Provided
  )

  override def requires: Plugins = plugins.JvmPlugin && AssemblyPlugin

  override def projectSettings = sparkDefaultSettings

  override def trigger = noTrigger

  lazy val sparkDefaultSettings = Seq(
    sparkVersion := "2.4.3",
    sparkComponents := Seq(),
    sparkComponentScope := defaultSparkComponentScope,
    sparkClassifier := "spark",
    sparkExclusionRules := Seq(),
    libraryDependencies ++= allSparkComponents.value,
    sparkValidateDeps := validateDependencies.value,
    publish := publish.dependsOn(assembly).value,
    publishLocal := publishLocal.dependsOn(assembly).value
  ) ++ sparkArtifactSettings ++ assemblySettings ++ runSettings

  private[this] lazy val sparkArtifactSettings =
    Seq(
      artifact in (Compile, assembly) := {
        val art = (artifact in (Compile, assembly)).value
        art.withClassifier(Some(sparkClassifier.value))
      }
    ) ++ addArtifact(artifact in (Compile, assembly), assembly)

  private[this] def validateDependencies = Def.task {
    val log = streams.value.log
    val currentSparkVersion = sparkVersion.value
    val componentScopes = sparkComponentScope.value

    log.info("Validating Spark dependencies...")

    val invalidDeps = libraryDependencies.value.view
      .filter(_.organization == SparkOrganization)
      .flatMap { dep =>
        val expectedScope = componentScopes.getOrElse(dep.name.substring(SparkPrefix.length), Compile)

        if (dep.revision != currentSparkVersion) {
          Seq(dep -> s"Spark module $dep is using wrong Spark version, expected: $currentSparkVersion.")
        } else if (!dep.configurations.exists(_ == expectedScope.name)) {
          Seq(dep -> s"Spark module $dep is using wrong scope, expected: $expectedScope.")
        } else Nil
      }.toList

    invalidDeps.map(_._2).foreach(log.error(_))
    if (invalidDeps.nonEmpty) {
      val helpMsg = {
        val modules = invalidDeps.map { case (dep, _) =>
          val moduleName = dep.name.substring(SparkPrefix.length)
          s""""$moduleName""""
        }
        SparkValidateSuggestionMsg ++ s"\n\t\tsparkComponents ++= $modules"
      }

      val errMsg = s"$helpMsg\nPlease resolve any previous conflicting Spark dependencies from your project's libraryDependencies."
      throw new MessageOnlyException(errMsg)
    }
  }.dependsOn(update)

  private[this] def allSparkComponents = Def.setting {
    val scopes = sparkComponentScope.value
    val components = sparkComponents.value
    val sparkV = sparkVersion.value
    val exclusions = sparkExclusionRules.value

    def sparkComponentLib(name: String, sparkV: String) =
      (SparkOrganization %% s"spark-$name" % sparkV % scopes.getOrElse(name, Compile)).excludeAll(exclusions: _*)

    components.map(sparkComponentLib(_, sparkV)) :+ sparkComponentLib("core", sparkV)
  }

  private[this] lazy val assemblySettings = Seq(
    assemblyMergeStrategy in assembly := {
      case PathList("org","aopalliance", xs @ _*) => MergeStrategy.last
      case PathList("javax", "inject", xs @ _*) => MergeStrategy.last
      case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
      case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
      case PathList("org", "apache", xs @ _*) => MergeStrategy.last
      case PathList("com", "google", xs @ _*) => MergeStrategy.last
      case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
      case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
      case PathList("com", "yammer", xs @ _*) => MergeStrategy.last

      case "about.html" => MergeStrategy.rename
      case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
      case "META-INF/mailcap" => MergeStrategy.last
      case "META-INF/mimetypes.default" => MergeStrategy.last
      case "plugin.properties" => MergeStrategy.last
      case "log4j.properties" => MergeStrategy.last

      // Netty
      case PathList("io", "netty", xs @ _*) => MergeStrategy.last
      case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.concat
      case PathList("META-INF", "native", xs @ _*) => MergeStrategy.last

      case x =>
        // Returns the default strategy
        val defaultStrategy = (assemblyMergeStrategy in assembly).value
        defaultStrategy(x)
    },
    test in assembly := {},
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    assembly := assembly.dependsOn(sparkValidateDeps).value
  )

  private[this] lazy val runSettings = Seq(
    run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))
      .dependsOn(sparkValidateDeps).evaluated,
    fork in (Compile, run) := true,
    javaOptions ++= Seq(
      "-Xms512M",
      "-Xmx2048M",
      "-XX:MaxPermSize=2048M",
      "-XX:+CMSClassUnloadingEnabled",
      "-XX:+UseConcMarkSweepGC"
    )
  )

}

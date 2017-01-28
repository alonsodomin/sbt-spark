package sbtspark

import sbt._
import sbtassembly.AssemblyPlugin

object SparkPlugin extends AutoPlugin {

  object autoImport extends SparkKeys

  import Keys._
  import AssemblyPlugin.autoImport._
  import autoImport._

  override def requires: Plugins = plugins.JvmPlugin && AssemblyPlugin

  override def projectSettings = sparkDefaultSettings

  override def trigger = allRequirements

  private[this] def sparkComponentLib(name: String, sparkV: String) =
    "org.apache.spark" %% s"spark-${name}" % sparkV % sparkComponentLibScope(name)

  private[this] def sparkComponentLibScope(name: String) = name match {
    case "core" | "sql" | "hive" | "streaming" => Provided
    case _                                     => Compile
  }

  private[this] def allSparkComponents(components: Seq[String], sparkV: String) =
    components.map(sparkComponentLib(_, sparkV)) :+ sparkComponentLib("core", sparkV)

  lazy val sparkDefaultSettings = Seq(
    sparkVersion := "1.6.3",
    sparkComponents := Seq(),
    libraryDependencies ++= allSparkComponents(sparkComponents.value, sparkVersion.value),
    run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run)).evaluated,
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

      case x =>
        // Returns the default strategy
        val defaultStrategy = (assemblyMergeStrategy in assembly).value
        defaultStrategy(x)
    },
    test in assembly := {},
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
  )

}

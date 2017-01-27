package sbtspark

import sbt._
import sbtassembly.AssemblyPlugin

object SparkPlugin extends AutoPlugin {

  object autoImport extends SparkKeys

  import Keys._
  import AssemblyPlugin.autoImport._
  import autoImport._

  lazy val sparkDefaultSettings = Seq(
    sparkVersion := "1.6.3",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core"      % sparkVersion.value % Provided,
      "org.apache.spark" %% "spark-sql"       % sparkVersion.value % Provided,
      "org.apache.spark" %% "spark-hive"      % sparkVersion.value % Provided,
      "org.apache.spark" %% "spark-streaming" % sparkVersion.value % Provided
    ),
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

  override def requires: Plugins = AssemblyPlugin

  override def projectSettings = sparkDefaultSettings

}

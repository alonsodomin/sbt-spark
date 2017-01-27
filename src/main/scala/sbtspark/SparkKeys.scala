package sbtspark

import sbt._

trait SparkKeys {
  val sparkVersion = settingKey[String]("Spark version")
  val sparkComponents = settingKey[Seq[String]]("Additional Spark components, i.e.: 'streaming', 'hive', etc.")
}

object SparkKeys extends SparkKeys

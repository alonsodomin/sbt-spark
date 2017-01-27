package sbtspark

import sbt._

trait SparkKeys {
  val sparkVersion = settingKey[String]("Spark version")
}

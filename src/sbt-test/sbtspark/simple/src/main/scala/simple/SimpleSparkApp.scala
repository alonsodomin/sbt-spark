package simple

import org.apache.spark._

object SimpleSparkApp {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setMaster("local[*]") // run locally with as many threads as CPUs
      .setAppName("Simple Spark 1.6.x Application") // name in web UI
      .set("spark.logConf", "true")
  }

}

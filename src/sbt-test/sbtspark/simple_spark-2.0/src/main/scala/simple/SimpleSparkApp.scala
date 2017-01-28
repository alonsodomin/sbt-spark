package simple

import org.apache.spark._

object SimpleSparkApp {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setMaster("local[1]")
      .setAppName("Simple Spark 2.0.x Application")
      .set("spark.logConf", "false")

    val sc = new SparkContext(conf)
    val count = sc.parallelize(Seq("Hello", "from", "Spark"), 1).count()
    println(s"Count result: $count")

    sc.stop()
  }

}

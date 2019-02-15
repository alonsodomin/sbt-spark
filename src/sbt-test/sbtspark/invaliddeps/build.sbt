scalaVersion := "2.11.8"

enablePlugins(SparkPlugin)

sparkVersion := "2.0.2"
sparkComponents := Seq("sql")

libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion.value
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.1.0"

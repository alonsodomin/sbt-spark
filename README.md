# SBT Spark

[![Build Status](https://travis-ci.org/alonsodomin/sbt-spark.svg?branch=master)](https://travis-ci.org/alonsodomin/sbt-spark)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.alonsodomin/sbt-spark/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.alonsodomin/sbt-spark)

This is a very simple plugin focused on adding all the boilerplate that you need to configure a Spark application
 in SBT so you do not have to.

## Getting started

Just add the following line to your `project/plugins.sbt` file:

```
addSbtPlugin("com.github.alonsodomin" % "sbt-spark" % "x.y.z")
```

Write your Spark app:

```scala
import org.apache.spark._

object SimpleSparkApp {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setMaster("local[1]")
      .setAppName("Simple Spark Application")
      .set("spark.logConf", "false")

    val sc = new SparkContext(conf)
    val count = sc.parallelize(Seq("Hello", "from", "Spark"), 1).count()
    println(s"Count result: $count")

    sc.stop()
  }

}
```

And run it!

```
sbt run
```

Voilà! All set, you are ready to start writing your awesome Spark application!

## Usage

### Choosing the Spark version:

By default the plugin will use Spark `1.6.3`. If you want to use a different version just put the following in your `build.sbt`:

```
sparkVersion := "2.0.2"
```

### Adding Spark components (modules) to my build

By default the plugin will only put `spark-core` in your classpath. If you want to use any other additional Spark module just
 use the following syntax in your `build.sbt` file:

```
sparkComponents += "sql"
```

or

```
sparkComponents ++= Seq("sql", "mllib")
```

In the last case, the plugin will also handle the dependency scope properly, meaning that the `sql` component will be
put in the `provided` scope whilst the `mllib` one will be packaged with your app.

### Packaging your application for deployment

`sbt-spark` uses `sbt-assembly` with some sensible defaults. To get a package that you can deploy in your Spark cluster,
just run `sbt assembly` from the command line.

If you need to customize your package, refer to [`sbt-assembly`'s website](https://github.com/sbt/sbt-assembly), all the
configuration keys from it are available as if you where using the plugin yourself.

## FAQ

### What does this plugin do?

Very little in fact, Spark applications all have the same setup boilerplate:

 * Add Spark dependencies and place them in the `provided` scope.
 * Configure `sbt-assembly` to package your application in an uberjar, which in itself means setting up the proper merge strategy and removing Scala's stdlib from the final artifact.
 * Re-configure SBT's `run` task so you still can run your app locally with the correct classpath.

It's a PITA to repeat this all over again every time you want to start a brand new Spark application, so `sbt-spark` does it for you. Simple as that.

### How is this different from `sbt-spark-packages`

Well, it's not really that different, this could even be considered a slimmed down version of the same "utility" but just
catering to a different audience.

`sbt-spark-packages` is meant to be used by developers that want to write extensions on top of Spark, _packages_ that other
Spark applications can use so it's very focused on giving you a good starting point plus a platform to ditribute your packages
to other users.

`sbt-spark` is meant to be a boilerplate-free starting point to write Spark applications. It's main audience is Spark developers
that write _end of the world_ Spark applications. Also, `sbt-spark` could be useful to support tooling around writing Spark applications,
(i.e.: test harness libraries) which still require the same starting point, but not fit into the _Spark package_ concept.

### I'm a library author targeting different versions of Spark, can this plugin support "cross Spark compiling"?

Not yet but but it might be doable. It's also questionable if this the right project for it.

## MIT License

Copyright 2017 Antonio Alonso Dominguez

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

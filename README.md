# SBT Spark

This is a very simple plugin focused on adding all the boilerplate that you need to configure a Spark application
 in SBT so you do not have to.

## Getting started

Just add the following line to your `project/plugins.sbt` file:

```
addSbtPlugin("[groupId]" % "sbt-spark" % "0.1.0")
```

Now enable the plugin in your `build.sbt` file:

```
enablePlugins(SparkPlugin)
```

Write your Spark app:

```
[SIMPLE EXAMPLE HERE]
```

And run it!

```
sbt run
```

Voila! All set, you are ready to start writing your awesome Spark application!

## FAQ

### What does this plugin do?

Very little in fact, Spark applications all have the same setup boilerplate:

 * Add Spark dependencies and place them in the `provided` scope.
 * Configure `sbt-assembly` to package your application in an uberjar, which in itself means setting up the proper merge strategy and removing Scala's stdlib from the final artifact.
 * Re-configure SBT's `run` task so you still can run your app locally with the correct classpath.

It's a PITA to repeat this all over again every time you want to start a brand new Spark application, so `sbt-spark` does it for you. Simple as that.

### How do I choose the version of Spark I want to use?

Just set it in your `build.sbt`:

```
sparkVersion := "2.0.2"
```

Reload SBT now and all your Spark dependencies should have been updated.

### I'm a library author targeting different versions of Spark, can this plugin support "cross Spark compiling"?

Not yet but I feel your pain. Enabling cross Spark compiling is just playing with the correct SBT settings so this is
 a feature not very difficult to implement and will be coming soon.

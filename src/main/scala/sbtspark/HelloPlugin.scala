package sbtspark

import sbt._
import Keys._

// http://www.scala-sbt.org/release/docs/Plugins.html
object HelloPlugin extends AutoPlugin {
  override lazy val projectSettings = Seq(commands += helloCommand)
  lazy val helloCommand =
    Command.command("hello") { (state: State) =>
      println("Hi!")
      state
    }
}

addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "2.0.0")
addSbtPlugin("com.github.gseitz" % "sbt-release"  % "1.0.11")
addSbtPlugin("com.jsuereth"      % "sbt-pgp"      % "1.1.2-1")
addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "2.0")

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

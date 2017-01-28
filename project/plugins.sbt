libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "1.6.0")
addSbtPlugin("com.github.gseitz" % "sbt-release"  % "1.0.3")
addSbtPlugin("com.jsuereth"      % "sbt-pgp"      % "1.0.0")
addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "1.1")

name := "serialization"

version := "1.0"

scalaVersion := "2.11.2"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases/"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.10"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.3.4"

libraryDependencies += "com.googlecode.scalascriptengine" %% "scalascriptengine" % "1.3.10"

libraryDependencies += "net.minidev" % "json-smart" % "1.2"

libraryDependencies += "com.github.pathikrit" %% "dijon" % "0.2.4"

libraryDependencies += "com.twitter" % "util-eval_2.10" % "6.22.1"

import sbt._
import Keys._
import sbt.Package.ManifestAttributes
import sbtassembly.Plugin._

object ApplicationBuild extends Build {

  val resolvers = Seq(
    "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
    "Typesafe Maven" at "http://repo.typesafe.com/typesafe/maven-releases",
    "The Buzz Media Repository" at "http://maven.thebuzzmedia.com",
    "spray nightlies repo" at "http://nightlies.spray.io/",
    "spray repo" at "http://repo.spray.io/",
    "Maven central" at "http://oss.sonatype.org/content/repositories/releases")

  val appName = "noname"
  val version = "0.1"
  val organization = "me.cheebo"

  val scalaStyleSettings = org.scalastyle.sbt.ScalastylePlugin.Settings

  val mlsCompile = InputKey[Unit]("scompile") in Compile

  val buildSettings = {
    Defaults.defaultSettings ++ scalaStyleSettings ++ assemblySettings ++
      Seq(
        fork := true,
        fork in Test := false,
        Keys.organization := organization,
        Keys.version := version,
        javaOptions in run ++= Seq(
          "-d64", "-Xmx6G", "-XX:+UseParallelGC"
        ),
        javacOptions ++= Seq(
          "-Xlint:unchecked"
        ),
        crossPaths := false,
        scalaVersion in ThisBuild := Versions.ScalaVersion,
        retrieveManaged := true,
        scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-language:postfixOps"),
        testOptions in Test := Nil,
        Keys.externalResolvers := Resolver.withDefaultResolvers(resolvers),
        libraryDependencies ++= appDependencies,
        packageOptions := Seq(ManifestAttributes(
          "Implementation-Vendor" -> organization,
          "Implementation-Title" -> appName,
          "Implementation-Version" -> version,
          "Main-Class" -> "define-main-class"
        )),
        exportJars := true,
        mlsCompile <<= org.scalastyle.sbt.PluginKeys.scalastyle dependsOn (compile in Compile),
        test in AssemblyKeys.assembly := {},
        AssemblyKeys.mergeStrategy in AssemblyKeys.assembly <<= (AssemblyKeys.mergeStrategy in AssemblyKeys.assembly) {
          (old) => {
            case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
            case _ => MergeStrategy.first
          }
        }
      )
  }

  import Versions._

  val appDependencies = Seq(

    "com.typesafe.akka" %% "akka-actor" % AkkaVersion ,
    "com.typesafe.akka" %% "akka-cluster" % AkkaVersion,
    "com.typesafe.akka" %% "akka-contrib" % AkkaVersion,
    "com.typesafe.akka" %% "akka-kernel" % AkkaVersion,

    "io.spray" % "spray-can" % SprayVersion,
    "io.spray" % "spray-routing" % SprayVersion,
    "io.spray" % "spray-client" % SprayVersion,

    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.0.13",

    "com.typesafe" % "config" % "1.0.2"
  )

  //  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.0.6"

  val main = Project(
    appName,
    file("."),
    settings = buildSettings)
}


object Versions {
  val SprayVersion = "1.2-20131011"
  val AkkaVersion = "2.2.2" //2.2.1
  val ScalaVersion = "2.10.3"
  val ScalatestVersion = "1.9.2" //1.9
}
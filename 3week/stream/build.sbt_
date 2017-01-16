import AssemblyKeys._

assemblySettings

lazy val root = (project in file(".")).settings(
 name := "stream",
 version := "1.0",
 scalaVersion := "2.11.7" ,
 mainClass in Compile := Some("logs.LogAnalyzerAppMain")
)

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.6.3"
libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % "1.3.0"
libraryDependencies += "com.github.scopt" %% "scopt" % "3.5.0"


// META-INF discarding
mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
 case x => MergeStrategy.first
}
}
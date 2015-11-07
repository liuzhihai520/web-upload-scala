name := """web-upload"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "commons-io"                  %     "commons-io"            %     "2.4",
  "commons-codec"               %     "commons-codec"         %     "1.6",
  "commons-collections"         %     "commons-collections"   %     "3.2.1",
  "commons-beanutils"           %     "commons-beanutils"     %     "1.8.3",
  "commons-lang"                %     "commons-lang"          %     "2.6",
  "net.sf.ezmorph"              %     "ezmorph"               %     "1.0.6",
  "log4j"                       %     "log4j"                 %     "1.2.17",
  "com.drewnoakes"              %     "metadata-extractor"    %     "2.6.2",
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
//routesGenerator := InjectedRoutesGenerator

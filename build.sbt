name := "Permutive Analytics Exercise"

version := "0.1"

scalaVersion := "2.12.8"
val circeVersion : String   = "0.9.3"


resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= {
  Seq(
    "org.typelevel"          %% "cats-core"   % "1.6.0",
    "org.typelevel"          %% "cats-effect" % "1.2.0",
    "co.fs2"                 %% "fs2-core"    % "1.0.4",
    "co.fs2"                 %% "fs2-io"      % "1.0.4",
    "com.github.nscala-time" %% "nscala-time" % "2.22.0",

    // Circe and et al
    "io.circe"               %% "circe-literal"    % circeVersion,
    "io.circe"               %% "circe-jawn"       % circeVersion,
    "io.circe"               %% "circe-generic"    % "0.9.0",
    "io.circe"               %% "circe-parser"     % "0.9.0",
    "org.scalactic"          %% "scalactic"        % "3.0.5",
    "org.scalatest"          %% "scalatest"        % "3.0.5" % "test"
  )
}
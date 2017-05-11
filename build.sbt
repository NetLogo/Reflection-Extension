enablePlugins(org.nlogo.build.NetLogoExtension)

scalaSource in Compile := baseDirectory.value / "src" / "main"

netLogoClassManager := "NetLogoReflectionScala"

netLogoVersion := "6.0.1-M1"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion := "2.12.1"
      ,version      := "0.1.0-SNAPSHOT"
    ))
    ,name :=  "reflection"
  )

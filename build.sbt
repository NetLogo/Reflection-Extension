enablePlugins(org.nlogo.build.NetLogoExtension)

name := "reflection"
version := "2.0.0"
isSnapshot := true

scalaVersion := "3.7.0"
Compile / scalaSource := baseDirectory.value / "src" / "main"
Test    / scalaSource := baseDirectory.value / "src" / "test"

netLogoVersion := "7.0.0"
netLogoClassManager := "org.nlogo.extensions.reflection.NetLogoReflectionScala"

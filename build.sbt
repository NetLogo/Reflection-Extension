enablePlugins(org.nlogo.build.NetLogoExtension)

scalaSource in Compile := baseDirectory.value / "src" / "main"

scalaSource in Test := baseDirectory.value / "src" / "test"

netLogoClassManager := "org.nlogo.extensions.reflection.NetLogoReflectionScala"

netLogoVersion := "6.0.1-M1"

netLogoTarget := NetLogoExtension.directoryTarget(baseDirectory.value)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion := "2.12.1"
      ,version      := "0.1.0-SNAPSHOT"
    ))
    ,name :=  "reflection"
    ,libraryDependencies ++= Seq(
      "org.picocontainer"  % "picocontainer" % "2.13.6" % "test",
      "org.scalatest" %% "scalatest" % "3.0.0" % "test",
      "org.ow2.asm" % "asm-all" % "5.0.3"  % "test"
    )
  )

val moveToRefDir = taskKey[Unit]("add all resources to Reflection directory")

val refDirectory = settingKey[File]("directory that extension is moved to for testing")

refDirectory := baseDirectory.value / "extensions" / "reflection"

moveToRefDir := {
  (packageBin in Compile).value
  val testTarget = NetLogoExtension.directoryTarget(refDirectory.value)
  testTarget.create(NetLogoExtension.netLogoPackagedFiles.value)
  val testResources = (baseDirectory.value / "test" ***).filter(_.isFile)
  for (file <- testResources.get)
    IO.copyFile(file, refDirectory.value / "test" / IO.relativize(baseDirectory.value / "test", file).get)
}

test in Test := {
  IO.createDirectory(refDirectory.value)
  moveToRefDir.value
  (test in Test).value
  IO.delete(refDirectory.value)
}

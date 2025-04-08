enablePlugins(org.nlogo.build.NetLogoExtension)

Compile / scalaSource := baseDirectory.value / "src" / "main"
Test    / scalaSource := baseDirectory.value / "src" / "test"

netLogoClassManager := "org.nlogo.extensions.reflection.NetLogoReflectionScala"

netLogoVersion := "6.4.0"

netLogoTarget := NetLogoExtension.directoryTarget(baseDirectory.value)

lazy val asmDependencies = {
  val asmVersion = "9.7.1"
  Seq(
    "org.ow2.asm" % "asm"         % asmVersion % "test",
    "org.ow2.asm" % "asm-commons" % asmVersion % "test",
    "org.ow2.asm" % "asm-util"    % asmVersion % "test"
  )
}

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion := "2.12.2"
      ,version      := "0.2.0-SNAPSHOT"
    ))
    ,name :=  "reflection"
    ,libraryDependencies ++= Seq(
      "org.picocontainer"  % "picocontainer" % "2.15.2" % "test",
      "org.scalatest" %% "scalatest" % "3.2.19" % "test"
    ) ++ asmDependencies
  )

val moveToRefDir = taskKey[Unit]("add all resources to Reflection directory")

val refDirectory = settingKey[File]("directory that extension is moved to for testing")

refDirectory := baseDirectory.value / "extensions" / "reflection"

moveToRefDir := {
  (Compile / packageBin).value
  val testTarget = NetLogoExtension.directoryTarget(refDirectory.value)
  testTarget.create(NetLogoExtension.netLogoPackagedFiles.value)
  val testResources = Path.allSubpaths(baseDirectory.value / "test").map(_._1).filter(_.isFile)
  for (file <- testResources.get)
    IO.copyFile(file, refDirectory.value / "test" / IO.relativize(baseDirectory.value / "test", file).get)
}

Test / test := {
  IO.createDirectory(refDirectory.value)
  moveToRefDir.value
  (Test / test).value
  IO.delete(refDirectory.value)
}

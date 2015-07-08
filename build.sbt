scalaVersion := "2.9.2"

scalaSource in Compile <<= baseDirectory(_ / "src/main/org/nlogo/extensions/reflection")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings",
                      "-encoding", "us-ascii")

libraryDependencies +=
  "org.nlogo" % "NetLogo" % "5.2" from
    "http://ccl.northwestern.edu/netlogo/5.2/NetLogo.jar"

artifactName := { (_, _, _) => "reflection.jar" }

packageOptions := Seq(
  Package.ManifestAttributes(
    ("Extension-Name", "reflection"),
    ("Class-Manager", "NetLogoReflection"),
    ("NetLogo-Extension-API-Version", "5.0")))

packageBin in Compile <<= (packageBin in Compile, baseDirectory, streams) map {
  (jar, base, s) =>
    IO.copyFile(jar, base / "reflection.jar")
    Process("pack200 --modification-time=latest --effort=9 --strip-debug " +
            "--no-keep-file-order --unknown-attribute=strip " +
            "reflection.jar.pack.gz reflection.jar").!!
    if(Process("git diff --quiet --exit-code HEAD").! == 0) {
      Process("git archive -o reflection.zip --prefix=reflection/ HEAD").!!
      IO.createDirectory(base / "reflection")
      IO.copyFile(base / "reflection.jar", base / "reflection" / "reflection.jar")
      IO.copyFile(base / "reflection.jar.pack.gz", base / "reflection" / "reflection.jar.pack.gz")
      Process("zip reflection.zip reflection/reflection.jar reflection/reflection.jar.pack.gz").!!
      IO.delete(base / "reflection")
    }
    else {
      s.log.warn("working tree not clean; no zip archive made")
      IO.delete(base / "reflection.zip")
    }
    
  }

cleanFiles <++= baseDirectory { base =>
  Seq(base / "reflection.jar",
      base / "reflection.jar.pack.gz",
      base / "reflection.zip") }
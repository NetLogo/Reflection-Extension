package org.nlogo.extensions.reflection

import org.nlogo.headless.TestLanguage

class Tests extends TestLanguage(Seq(new java.io.File("tests.txt").getCanonicalFile))

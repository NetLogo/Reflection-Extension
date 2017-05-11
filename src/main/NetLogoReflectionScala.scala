package org.nlogo.extensions.reflection

import org.nlogo.core.{ Program, Syntax }
import org.nlogo.core.Syntax.{ NumberType, ListType }
import org.nlogo.app.App
import org.nlogo.nvm.ExtensionContext
import org.nlogo.api.{ LogoListBuilder, Reporter, Context, Argument, PrimitiveManager, DefaultClassManager, ExtensionException }
import org.nlogo.api.ScalaConversions._

class NetLogoReflectionScala extends DefaultClassManager {
  override def load(manager: PrimitiveManager) {
    manager.addPrimitive("globals", new Globals)
    manager.addPrimitive("breeds", new Breeds)
    manager.addPrimitive("procedures", new Procedures);
    manager.addPrimitive("current-procedure", new CurrentProcedure);
    manager.addPrimitive("callers", new Callers);
  }
}

class Globals extends Reporter {
  override def getSyntax = Syntax.reporterSyntax(ret = ListType)
  override def report(args: Array[Argument], context: Context): AnyRef = {
    val observer = App.app.workspace.world.observer
    (0 until observer.getVariableCount).map(observer.variableName).toLogoList
  }
}

class Breeds extends Reporter {
  override def getSyntax = Syntax.reporterSyntax(ret = ListType)
  override def report(args: Array[Argument], context: Context): AnyRef = {
    val breeds = new LogoListBuilder
    // add turtle vars as a separate tuple
    val turtleInfo = new LogoListBuilder
    turtleInfo.add("TURTLES")
    val turtleVars = App.app.workspace.world.program.turtleVars.keys.toVector.toLogoList
    turtleInfo.add(turtleVars)
    breeds.add(turtleInfo.toLogoList)
    // now the rest of the breeds
    val otherBreeds = App.app.workspace.world.program.breeds.map {
      case (name, vars) =>
        val breedInfo = new LogoListBuilder
        breedInfo.add(name)
        breedInfo.add(vars.owns.toLogoList)
        breedInfo.toLogoList
    }
    otherBreeds.foreach(breeds.add)
    breeds.toLogoList
  }
}

class Procedures extends Reporter {
  override def getSyntax = Syntax.reporterSyntax(ret = ListType)
  override def report(args: Array[Argument], context: Context): AnyRef = {
    App.app.workspace.procedures.map {
      case (name, procedure) =>
        val procedureInfo = new LogoListBuilder
        procedureInfo.add(name)
        procedureInfo.add(if (procedure.isReporter) "REPORTER" else "COMMAND")
        procedureInfo.add(procedure.agentClassString)
        // args contains dummies (temp 'lets') so we don't include them.
        // localsCount contains number of lets so we just subtract that
        val argsCount = procedure.args.size - procedure.localsCount
        val args = procedure.args.take(argsCount).toLogoList
        procedureInfo.add(args)
        procedureInfo.toLogoList
    }.toVector.toLogoList
  }
}

class CurrentProcedure extends Reporter {
  override def getSyntax = Syntax.reporterSyntax(ret = ListType)
  override def report(args: Array[Argument], context: Context): AnyRef = {
    context match {
      case extContext: ExtensionContext => extContext.nvmContext.activation.procedure.name;
      case _ => throw new ExtensionException(s"Unknown context given : $context")
    }
  }
}

class Callers extends Reporter {
  override def getSyntax = Syntax.reporterSyntax(ret = ListType)
  override def report(args: Array[Argument], context: Context): AnyRef = {
    val callers = new LogoListBuilder
    context match {
      case extContext: ExtensionContext => {
        var activation = extContext.nvmContext.activation
        while (activation != null && activation.procedure != null) {
          callers.add(activation.procedure.name)
          activation = activation.parent
        }
        callers.toLogoList
      }
      case _ => throw new ExtensionException(s"Unknown context given : $context")
    }
  }
}

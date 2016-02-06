package net.pushl.shigure

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.text._
import scalafx.scene.layout._
import scalafx.scene.paint._
import scalafx.scene._
import scalafx.stage.Screen
import scalafx.scene.control._
import scalafx.scene.transform._

import net.pushl.shigure.beamer._
import net.pushl.shigure.util.Util
import net.pushl.shigure.general._

import javax.script.ScriptEngineManager

class DevelopEnvironment extends SplitPane {
  val fixed = new FixedSizePaddingPane(120, 80)
  val texts = new TextArea()

  val e = new ScriptEngineManager().getEngineByName("scala")
  e.asInstanceOf[scala.tools.nsc.interpreter.IMain].settings.usejavacp.value = true

  import BImplicits._
  def refresh(n: Node): Unit = {
    fixed.zoompane.group.children.setAll(n)
    ()
  }

  texts.text.onChange {
    val ret = e.eval(texts.text.value)
    println(ret)
    println(ret.isInstanceOf[Node])
    if(ret.isInstanceOf[Node])
      refresh(ret.asInstanceOf[Node])
  }

  items.addAll(fixed, texts)
}

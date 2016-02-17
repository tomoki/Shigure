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
import scalafx.geometry.Orientation

import net.pushl.shigure.beamer._
import net.pushl.shigure.general._

import javax.script.ScriptEngineManager
import javax.script.ScriptException

class DevelopEnvironment extends SplitPane {
  val fixed = new FixedSizePaddingPane(120, 80)

  val error = new TextArea {
    style = "-fx-font: normal 12pt 'monospace'"
  }
  val texts = new TextArea {
    style = "-fx-font: normal 12pt 'monospace'"
  }
  val editors = new SplitPane()
  editors.orientation = Orientation.VERTICAL
  editors.items.setAll(texts, error)

  val e = new ScriptEngineManager().getEngineByName("scala")
  val interpreter = e.asInstanceOf[scala.tools.nsc.interpreter.IMain]
  interpreter.settings.usejavacp.value = true

  import BImplicits._
  def refresh(n: Node): Unit = {
    fixed.zoompane.group.children.setAll(n)
    ()
  }

  e.eval("import net.pushl.shigure.beamer._; import net.pushl.shigure.general._; import BImplicits._")
  texts.text.onChange {
    try {
      val ret = interpreter.eval(texts.text.value)
      error.text = ret.toString
      if(ret.isInstanceOf[Node])
        refresh(ret.asInstanceOf[Node])
      ()
    } catch {
      case ex: ScriptException => {
        error.text = ex.getMessage()
      }
    }
  }

  items.addAll(fixed, editors)
}

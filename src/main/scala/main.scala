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

import javax.script.ScriptEngineManager

import net.pushl.shigure.general._
object Main extends JFXApp {
  val want_w_mm = 160.0
  val want_h_mm = 90.0

  stage = new PrimaryStage {
    title = "Scalpre"

    width  onChange onScale
    height onChange onScale
    def onScale() : Unit = {
      fixed.resize(width.value, height.value)
    }
    val fixed = new FixedSizePaddingPane(want_w_mm, want_h_mm)

    fixed.zoompane.group.children.setAll(
      new VBox (
        new Text{
          text  = "Shigure: Extensible Presentation Tool Written in Scala"
          style = "-fx-font: normal 14pt 'Migu 1M'"
          fill  = Color.Blue
        },
        new BeamerBox(new Text{
                        text = "This is title"
                        style = "-fx-font: normal 11pt 'Migu 1P'"
                        fill  = Color.Green
                      },
                      new BeamerItemize(() => new Text("-> "))(
                        new Text("This"),
                        new Text("is"),
                        new Text("itemize")
                      ))))

    scene = new Scene {
      root = fixed
    }
    // val e = new ScriptEngineManager().getEngineByName("scala")
    // e.asInstanceOf[scala.tools.nsc.interpreter.IMain].settings.usejavacp.value = true
    // e.put("group: Group", group)
    // println(e.eval("import scalafx.scene.text._; group.children.add(new Text(\"a\"))"))
  }
}

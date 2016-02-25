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
import scalafx.beans.property._
import scalafx.animation.{Interpolator, Timeline}
import javafx.scene.image._
import scalafx.scene.input._

import net.pushl.shigure.beamer._
import javax.script.ScriptEngineManager

import net.pushl.shigure.general._

object Main extends JFXApp {
  val want_w_mm = 160.0
  val want_h_mm = 90.0

  stage = new PrimaryStage {
    title = "Shigure"
    scene = new Scene {
      root = new DevelopEnvironment()
    }
  }
}

package scalpre

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.text._
import scalafx.scene.layout._
import scalafx.scene.paint._
import scalafx.scene.control._

object Win {
  type Win = Scene
  def apply(f : Win => Win) : Win = {
    f(new Win())
  }
}

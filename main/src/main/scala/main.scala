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
    title = "Scalpre"
    def onScale() : Unit = {
      fixed.resize(width.value, height.value)
    }
    import SizeImplicits._
    import BImplicits._
    import scala.language.postfixOps
    import scalafx.Includes._

    width  onChange onScale
    height onChange onScale
    val fixed = new FixedSizePaddingPane(want_w_mm, want_h_mm)
    implicit val size_info = new {
      val dpi    = DoubleProperty(Screen.primary.dpi)
      val width  = DoubleProperty(Util.mmToPx(want_w_mm , dpi.value))
      val height = DoubleProperty(Util.mmToPx(want_h_mm , dpi.value))
    } with FrameSizeInfo

    implicit val beamer_theme = new {
      val itemize_head   = () => new Text("◼ ")
      val enumerate_head = (i: Int) => new Text((i + 1).toString + ":")
    } with BeamerTheme

    val image = new ImageView("http://pushl.net/blog/12/yukari_san.png")
    image.preserveRatio = true
    image.fitWidth  <== size_info.width
    image.fitHeight <== size_info.height

    val it = BVar[BItemize]("itemiez")
    val ss = BVar[TextFlow]("src", "souce code")
    val frame = (
      BFrame ("Column sample")
        * (it :=
             BItemize
             - "one"
             - "two"
             - "three")
        * (BEnum
             - "1"
             - "2"
             - "3")
        * (BVSpace (14.0 pt))
    )

    val timeline = new Timeline {
      cycleCount = Timeline.Indefinite
      autoReverse = true
      keyFrames = Seq(
        at(1 s) {it(0).opacity -> 0},
        at(2 s) {it(1).opacity -> 0},
        at(3 s) {it(2).opacity -> 0}
      )
    }
    timeline.play()

    fixed.set(frame)
    scene = new Scene {
      root = fixed
    }
  }
}

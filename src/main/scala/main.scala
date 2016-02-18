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
    width  onChange onScale
    height onChange onScale
    val fixed = new FixedSizePaddingPane(want_w_mm, want_h_mm)

    implicit val size_info = new {
      // TODO: should polling which screen we see?
      val dpi    = DoubleProperty(Screen.primary.dpi)
      val width  = DoubleProperty(Util.mmToPx(want_w_mm , dpi.value))
      val height = DoubleProperty(Util.mmToPx(want_h_mm , dpi.value))
    } with FrameSizeInfo

    import BImplicits._

    import scala.language.postfixOps
    import scalafx.Includes._

    val it = BVar[BItemize]("itemiez")
    val ss = BVar[TextFlow]("src", "souce code")
    val frame = (
      BFrame ("Column sample")
        * (it :=
             BItemize {"- "}
             - "one"
             - "two"
             - "three")
        * (BVSpace (14.0 pt))
        * ss
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

    // // fixed.zoompane
    // .group.children.setAll(
    //   BFrame (new Text{
    //             text  = "Shigure: Extensible Presentation Tool Written in Scala"
    //             style = "-fx-font: normal 14pt 'Migu 1M'"
    //             fill  = Color.Blue})
    //     * "test"
    // )
    // fixed.zoompane.group.children.setAll(
    //   BFrame (new Text{
    //             text  = "Shigure: Extensible Presentation Tool Written in Scala"
    //             style = "-fx-font: normal 14pt 'Migu 1M'"
    //             fill  = Color.Blue})
    //     * BBox ("Following is enumerate")
    //            (BEnum {i => s" $i: "}
    //               - "hello"
    //               - "new"
    //               - "world")
    //            (BVSpace (14.0 pt))
    //            (BItemize {s" -> "}
    //               - "hello"
    //               - "new"
    //               - "world")
    // )

    scene = new Scene {
      root = fixed
    }
  }
}


// object Main extends JFXApp {
//   val want_w_mm = 160.0
//   val want_h_mm = 90.0

//   stage = new PrimaryStage {
//     title = "Shigure"
//     val de = new DevelopEnvironment()
//     de.texts.text.value = """
// val ret =
//   BFrame ("test") (
//   BBox ("Following is enumerate")
//        (BEnum {i => s" $i: "}
//           - "hello"
//           - "new"
//           - "world")
//        (BVSpace (20))
//        (BItemize {s" -> "}
//           - "hello"
//           - "new"
//           - "world")
// )
// ret
//     """
//     scene = new Scene {
//       root = de
//     }
//   }
// }

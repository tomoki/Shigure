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

    import BImplicits._

    val l = BVar("left_column")
    val r = BVar("right_column")

    fixed.zoompane.group.children.setAll(
      BFrame (new TextFlow(new Text{
                text  = "Shigure: Extensible Presentation Tool Written in Scala"
                style = "-fx-font: normal 14pt 'Migu 1M'"
                fill  = Color.Blue})) (
        BColumns(0.5, 0.5)
                (l := "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL")
                (r := "RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR")
      )
    )
    // println(l)
    // println(r)

    // fixed.zoompane.group.children.setAll(
    //   BFrame (new Text{
    //             text  = "Shigure: Extensible Presentation Tool Written in Scala"
    //             style = "-fx-font: normal 14pt 'Migu 1M'"
    //             fill  = Color.Blue}) (
    //     BBox ("Following is enumerate")
    //          (BEnum {i => s" $i: "}
    //             - "Animations are very commonly used in creating good UIs, which is why JavaFX Script had a built-in construct to simplify the creation of animations. ScalaFX has a similar syntax that allows you to quickly and easily create animations, which is used in the ColorfulCircles? example:"
    //             - "new"
    //             - "world")
    //          (BVSpace (10.0))
    //          (BItemize {s" -> "}
    //             - "hello"
    //             - "new"
    //             - "world")
    //   ))
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

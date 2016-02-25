package net.pushl.shigure

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.text._
import scalafx.scene._
import scalafx.stage.Screen
import scalafx.beans.property._
import scalafx.animation._
import scalafx.scene.paint.Color
import scalafx.scene.input._
import scalafx.beans.property._
import javafx.scene.image._

import net.pushl.shigure.beamer._
import javax.script.ScriptEngineManager

import net.pushl.shigure.general._

object Main extends JFXApp {
  val want_w_mm = 128.0
  val want_h_mm = 96.0

  stage = new PrimaryStage {
    title = "Scalpre"

    import BImplicits._
    import scala.language.postfixOps
    import scalafx.Includes._

    val fixed = new FixedSizePaddingPane(want_w_mm, want_h_mm)
    // fixed.style = "-fx-background-color: #0000ff"

    import SizeImplicits._
    width  onChange onScale
    height onChange onScale
    def onScale() : Unit = {
      fixed.resize(width.value, height.value)
    }
    scene = new Scene {
      root = fixed
    }
    implicit val size_info = new {
      val dpi    = DoubleProperty(Screen.primary.dpi)
      val width  = DoubleProperty(Util.mmToPx(want_w_mm , dpi.value))
      val height = DoubleProperty(Util.mmToPx(want_h_mm , dpi.value))
    } with FrameSizeInfo

    implicit val beamer_theme = new {
      val itemize_head   = () => new Text("◼ ")
      val enumerate_head = (i: Int) => new Text((i + 1).toString + ": ")
    } with BeamerTheme

    val image = new ImageView("http://pushl.net/blog/12/yukari_san.png")
    image.preserveRatio = true
    image.fitWidth  <== size_info.width
    image.fitHeight <== size_info.height

    def titlize(s: String) : TextFlow =
      new TextFlow(
        new Text {
          text=s
          style="-fx-font-size: 20pt; -fx-text-fill: #006464;"
          fill = Color.Blue})

    class GoodBadItemize extends scalafx.scene.layout.VBox {
      fillWidth = true
      def addItem(is_good:Boolean)(n: Node) : GoodBadItemize = {
        val adds = if(is_good)
          new TextFlow(new Text{
                         text="✔ "
                         fill=Color.Green}, n)
        else
          new TextFlow(new Text{
                         text="✘ "
                         fill=Color.Red}, n)
        children.add(adds)
        this
      }
      def -(n: Node) = addItem(false)(n)
      def +(n: Node) = addItem(true)(n)
    }
    object GoodBadItemize {
      def -(n: Node) = (new GoodBadItemize) - n
      def +(n: Node) = (new GoodBadItemize) + n
    }

    def titleFrame : BFrame = {
      val ret = (
        BFrame ("")
          * titlize("Shigure: Modular Presentation \n Live Programming Environment")
          * (BVSpace (60.0 pt))
          * " Tomoki Imai"
          * "   (Tokyo Institute of Technology, Masuhara lab)"
      )
      ret
    }

    def aboutMe : BFrame = {
      val ret = (
        BFrame (titlize("About me"))
          * (BItemize
               - "name: Tomoki Imai (今井 朝貴)"
               - "job : Master course student at Tokyo Tech Masuhara.Lab")
      )
      ret
    }

    def caution : BFrame = {
      val ret = (
        BFrame (titlize(""))
          * BVSpace (50 pt)
          * "I will talk about is ongoing work."
          * (BItemize (() => " => ")
               - "Some feature are not yet implemented."
               - "We need your feedback :)"
          ))
      ret
    }
    def problems : BFrame = {
      val picture_ratio = 0.4
      val ret = (
        BFrame (titlize("Problem: Making Slides is a Pain"))
          * "There are some presentation tools, but I don't like them."
          * BBox ("PowerPoint")
                 (BColumns (picture_ratio, 1-picture_ratio)
                           (" picture here")
                           (GoodBadItemize
                              + "Good GUI  -> easy to layout"
                              - "No module -> hard to re-use"
                              - "Can't use with my favorite text editors"
                              - "Poor animation tool"
                           )
                 )
          * BVSpace (12 pt)
          * BBox ("Latex Beamer")
                 (BColumns (picture_ratio, 1-picture_ratio)
                           (" picture here")
                           (GoodBadItemize
                              + "Can use with my favorite text editors"
                              + "Version control like Git"
                              - "Poor programming language"
                              - "Hard to make graphical presentation"
                              - "Limitation of PDF"
                           )
                 )
          * BVSpace (12 pt)
          * "(How about markdown, Smalltalk?)"
      )
      ret
    }

    val my_approach = (
      BFrame ("My Approach: DSL and PE for Presentation in Scala")
    )

    val frames = Vector(
      titleFrame,
      aboutMe,
      caution,
      problems,
      my_approach
    )
    val current_page = IntegerProperty(0)
    scene().onKeyPressed = (event: KeyEvent) => {
      event.getCode match {
        // Bug in ScalaFX?
        case javafx.scene.input.KeyCode.RIGHT => {
          current_page.value = ((current_page.value) + 1) min (frames.size - 1)
        }
        case javafx.scene.input.KeyCode.LEFT => {
          current_page.value = ((current_page.value) - 1) max 0
        }
        case _ => Console.err.println("Unknown Key: " + event.getCode)
      }
    }
    current_page onChange  {
      fixed.set(frames(current_page.value))
    }
    fixed.set(frames(0))
  }
}

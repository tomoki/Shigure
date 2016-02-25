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
import scalafx.scene.control._
import scalafx.beans.value._
import scalafx.beans.binding._
import scalafx.Includes._
import scalafx.application.{Platform, JFXApp}
import javafx.scene.image._


import javax.script.ScriptEngineManager
import javax.script.ScriptException

import net.pushl.shigure.general._
import net.pushl.shigure.beamer._

object Main extends JFXApp {
  val want_w_mm = 128.0
  val want_h_mm = 96.0

  stage = new PrimaryStage {
    title = "Scalpre"

    import BImplicits._
    import scala.language.postfixOps
    import scalafx.Includes._

    val fixed = new FixedSizePaddingPane(want_w_mm, want_h_mm)

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

    def titlize(s: String) : TextFlow =
      new TextFlow(
        new Text {
          text  = s
          style = "-fx-font-size: 17pt"
          fill  = Color.Blue})

    def boxtitle(s: String, c: Color) : TextFlow =
      new TextFlow(
        new Text {
          text  = s
          style = "-fx-font-size: 12pt"
          fill  = c})

    class GoodBadItemize extends scalafx.scene.layout.VBox {
      fillWidth = true
      def addItem(is_good:Boolean)(n: Node) : GoodBadItemize = {
        val (tex, col) =
          if(is_good) ("✔ ", Color.Green)
          else        ("✘ ", Color.Red)
        children.add(new TextFlow(new Text{
                                    text=tex
                                    fill=col}, n))
        this
      }
      def -(n: Node) = addItem(false)(n)
      def +(n: Node) = addItem(true)(n)
    }
    object GoodBadItemize {
      def -(n: Node) = (new GoodBadItemize) - n
      def +(n: Node) = (new GoodBadItemize) + n
    }

    // Workaround: I need better way.
    class BImage(uri: String, width: NumberBinding) extends ImageView(uri) {
      this.fitWidth <== width
      setPreserveRatio(true)
    }
    object BImage {
      def apply(uri: String, width: NumberBinding) =
        new BImage(uri, width)
    }


    def titleFrame : BFrame = {
      val ret = (
        BFrame ("")
          * titlize("Shigure: Towards Modular Presentation \n Live Programming Environment")
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
          * "I will talk about just ideas."
          * (BItemize (() => " => ")
               - "Most of features are not implemented yet."
               - "We need your feedback :)")
          * BVSpace (50 pt)
          * (BItemize
               - "Talk: 10 minutes"
               - "Questions, Comments, Demos: 5 minutes")
      )
      ret
    }

    def problems : BFrame = {
      val picture_ratio = 0.35
      val powerpoint_url = getClass.getResource("/powerpoint.png").toURI().toString
      val beamer_url     = getClass.getResource("/beamer.png").toURI().toString
      val ret = (
        BFrame (titlize("Problem: Making Slides is a Pain"))
          * "There are some presentation tools, but I don't like them."
          * (BColumns (picture_ratio, 1-picture_ratio)
                      (BImage(powerpoint_url, size_info.width * picture_ratio))
                    (BBox (boxtitle("  PowerPoint", Color.Black))
                            (GoodBadItemize
                               + "Good GUI  -> easy to layout"
                               - "No module -> hard to re-use"
                               - "Can't use with my favorite text editors"
                               - "Poor animation tool (low-level operation)"
                            ))
          )
          * BVSpace (8 pt)
          * (BColumns (picture_ratio, 1-picture_ratio)
                           (BImage(beamer_url, size_info.width * picture_ratio))
                    (BBox (boxtitle("  LaTeX Beamer", Color.Black))
                           (GoodBadItemize
                              + "Can use with my favorite text editors"
                              + "Version control like Git"
                              - "Poor programming language"
                              - "Hard to make graphical presentation"
                              - "Limitation of PDF"
                           )
                    ))
          * BVSpace (8 pt)
          * "(How about markdown, Smalltalk?)"
      )
      ret
    }
    def approach1 : BFrame = {
      val ret = (
        BFrame (titlize("Method: DSL and Live Programming Environment for Presentation in Scala"))
          * "Scala is a object-oriented and functional programming language."
          * "(e)DSL := (embedded) Domain Specific Language"
          * "Live Programming Environment:"
      )
      ret
    }

    def example: BFrame = {
      val init = """
import net.pushl.shigure.general._
import net.pushl.shigure.beamer._
import scalafx.beans.property._
import scalafx.scene.text._
import SizeImplicits._
import BImplicits._
import scalafx.stage.Screen
import scalafx.scene._
import scalafx.scene.paint.Color
implicit val size_info = new {
  val dpi    = DoubleProperty(Screen.primary.dpi)
  val width  = DoubleProperty(Util.mmToPx(64 , dpi.value))
  val height = DoubleProperty(Util.mmToPx(48 , dpi.value))
} with FrameSizeInfo

implicit val beamer_theme = new {
  val itemize_head   = () => new Text("◼ ")
  val enumerate_head = (i: Int) => new Text((i + 1).toString + ": ")
} with BeamerTheme

class GoodBadItemize extends scalafx.scene.layout.VBox {
  fillWidth = true
  def addItem(is_good:Boolean)(n: Node) : GoodBadItemize = {
    val (tex, col) =
      if(is_good) ("✔ ", Color.Green)
      else        ("✘ ", Color.Red)
    children.add(new TextFlow(new Text{
                                text=tex
                                fill=col}, n))
    this
  }
  def -(n: Node) = addItem(false)(n)
  def +(n: Node) = addItem(true)(n)
}
object GoodBadItemize {
  def -(n: Node) = (new GoodBadItemize) - n
  def +(n: Node) = (new GoodBadItemize) + n
}

def frame = {
  val ret = (
    BFrame ("this is title")
    * (BItemize
       - "○good"
       - "×bad"
    )
  )
  ret
}
frame
"""
      val textarea   = (new TextArea(init))
      textarea.style = "-fx-font-family: monospace"
      textarea.focusTraversable = false

      val wrapper    = (new scalafx.scene.layout.VBox())
      scalafx.scene.layout.VBox.setVgrow(textarea, scalafx.scene.layout.Priority.Always)

      val e = new ScriptEngineManager().getEngineByName("scala")

      val interpreter = e.asInstanceOf[scala.tools.nsc.interpreter.IMain]
      interpreter.settings.usejavacp.value = true
      def read_and_refresh() = {
        val text = textarea.text.value
        import scala.concurrent._
        import ExecutionContext.Implicits.global 
        Future {
          refresh(text)
        }
      }
      def refresh(to_eval: String) = {
        interpreter.reset()
        try {
          val r = interpreter.eval(to_eval)
          if(r.isInstanceOf[Node])
            Platform.runLater {
              wrapper.children.setAll(r.asInstanceOf[Node])
            }
          ()
        } catch {
          case ex: ScriptException => {
            Console.err.println(ex.getMessage())
          }
        }
      }
      val evalbutton = (new Button {
                          text     = "▶ Eval"
                          onAction = handle {
                            read_and_refresh()
                          }
                        })

      val live_check = (new CheckBox("Live(experimental)"))
      textarea.text.onChange {
        if(live_check.selected.value){
          val _ = read_and_refresh()
        }
        ()
      }


      textarea onKeyReleased = (event: KeyEvent) => {
        event.getCode match {
          case javafx.scene.input.KeyCode.ESCAPE =>
            evalbutton.requestFocus() // workaround.
          case _ => {}
        }
      }

      val right = BVar[BBox]()
      val ret = (
        BFrame (titlize("Example: Good/Bad Itemize Module"))
          * (BColumns (0.5, 0.5)
                      (wrapper)
                      (right := (BBox (textarea)
                                   (BColumns (0.3, 0.7)
                                             (evalbutton)
                                             (live_check)
                                   )))
          )
      )
      right.get.prefHeight <== (size_info.height - (40 pt))
      ret
    }

    val testing = (
      BFrame (titlize ("Idea: \"Assert\" in Presentation?"))
        * "Context-dependent spell check"
        * "Animation testing"
        * "Live supports"
    )

    val frames = Vector(
      titleFrame,
      aboutMe,
      caution,
      problems,
      approach1,
      example,
      testing
      // implementation
    )
    val current_page = IntegerProperty(0)
    def goNext() : Unit = {
      current_page.value = ((current_page.value) + 1) min (frames.size - 1)
    }
    def goPrev() : Unit = {
      current_page.value = ((current_page.value) - 1) max 0
    }
    scene().onKeyPressed = (event: KeyEvent) => {
      event.getCode match {
        // Bug in ScalaFX?
        case javafx.scene.input.KeyCode.RIGHT =>
          goNext()
        case javafx.scene.input.KeyCode.LEFT =>
          goPrev()
        case _ => Console.err.println("Unknown Key: " + event.getCode)
      }
    }
    current_page onChange  {
      fixed.set(frames(current_page.value))
    }
    fixed.set(frames(0))
  }
}

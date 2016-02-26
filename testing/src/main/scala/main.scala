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

    class ProsConsItemize extends BItemize(() => "") {
      fillWidth = true
      def addItem(is_good:Boolean)(n: Node) : ProsConsItemize = {
        val (tex, col) =
          if(is_good) ("✔ ", Color.Green)
          else        ("✘ ", Color.Red)
        children.add(new TextFlow(new Text{
                                    text = tex
                                    fill = col}, n))
        this
      }
      override def -(n: Node) = addItem(false)(n)
      def +(n: Node) = addItem(true)(n)
    }
    object ProsConsItemize {
      def -(n: Node) = (new ProsConsItemize) - n
      def +(n: Node) = (new ProsConsItemize) + n
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
                            (ProsConsItemize
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
                           (ProsConsItemize
                              + "Can use with my favorite text editors"
                              + "Version control like Git"
                              - "Poor programming language"
                              - "Hard to make graphical presentation"
                              - "Limitation of PDF"
                           )
                    ))
          * BVSpace (8 pt)
          * "(How about markdown, webcomponents, Smalltalk?)"
      )
      ret
    }
    class QA extends scalafx.scene.layout.VBox {
      children.add(new Text("Question "))
      val right_padding = new scalafx.scene.layout.VBox()
      val columns = (BColumns (0.02, 0.98)
                              ("")
                              (right_padding))
      children.add(columns)
      def question(question: Node) : QA = {
        children.set(0, question)
        this
      }
      def answer(answer: Node)     : QA = {
        right_padding.children.add(answer)
        this
      }
      def content(content: Node)   : QA = {
        right_padding.children.add(content)
        this
      }
    }
    class SourceCode extends TextFlow{
      style="-fx-background-color:#FFFF66;-fx-font-family: monospace"
      def fromString(s: String) = {
        this.children.add(new Text(s))
      }
    }
    object SourceCode {
      def fromString(s: String) : SourceCode = {
        val ret = new SourceCode()
        ret.fromString(s)
        ret
      }
      def fromPath(s: String) : SourceCode = {
        sys.error("Not implemented")
      }
    }
    def approach1 : BFrame = {
      val sql_sample =
"""SELECT * FROM STATION
WHERE 50 < (SELECT AVG(TEMP_F) FROM STATS
WHERE STATION.ID = STATS.ID);"""
      val bnf_sample =
"""<expr> ::= <term>|<expr><addop><term>
<integer> ::= <digit>|<integer><digit>"""
      val shigure_sample =
"""BFrame (titlize("About me"))
  * BVSpace (30 pt)
  * (BItemize
      - "name: Tomoki Imai (今井 朝貴)"
      - "job : Master course student at Tokyo Tech Masuhara.Lab")"""

      val ret = (
        BFrame (titlize("Method: EDSL and Live Programming Environment for Presentation in Scala"))
          * (new QA()
               .question(boxtitle("Q. What is (E)DSL?", Color.Green))
               .answer("A. (E)DSL stands for (Embedded) Domain Specific Language")
               .content(BBox(BVSpace (5 pt))
                            ("ex1. SQL, DSL for manipulating database (it uses own parser)")
                            (SourceCode.fromString(sql_sample))
                            (BVSpace (3 pt))
                            ("ex2. BNF, DSL for specifying syntax (it uses own parser)")
                            (SourceCode.fromString(bnf_sample))
                            (BVSpace (3 pt))
                            ("ex3. Shigure, EDSL for making slides (uses host language syntax)")
                            (SourceCode.fromString(shigure_sample))
             )
          )

      )
      ret
    }
    def approach2 : BFrame = {
      val ret = (
        BFrame (titlize("Method: EDSL and Live Programming Environment for Presentation in Scala"))
          * (new QA().question(boxtitle("Q. Why Scala (a object-oriented function language)?", Color.Green))
                     .answer("A. It is well-designed modern language, and I love it :).")
                     .content((ProsConsItemize
                                 + "Object-oriented, Functional"
                                 + "Modularity (package, trait)"
                                 + "(E)DSL support"
                                 + "Implicitly"
                                 + "Macros"
                                 + "Hot-swapping, interpreters"
                              )))
          * (BVSpace (40 pt))
          * "   I will talk about programming environment later..."
      )
      ret
    }

    def example: BFrame = {
      val init =
"""import net.pushl.shigure.general._
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

class ProsConsItemize extends BItemize(() => "") {
  def addItem(is_good:Boolean)(n: Node) : ProsConsItemize = {
    val (tex, col) =
      if(is_good) ("✔ ", Color.Green)
      else        ("✘ ", Color.Red)
    children.add(new TextFlow(new Text{
                                text = tex
                                fill = col}, n))
    this
  }
  override def -(n: Node) = addItem(false)(n)
  def +(n: Node) = addItem(true)(n)
}
object ProsConsItemize {
  def -(n: Node) = (new ProsConsItemize) - n
  def +(n: Node) = (new ProsConsItemize) + n
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
frame"""
      val textarea   = (new TextArea(init))
      textarea.style = "-fx-font-family: monospace"
      textarea.focusTraversable = false

      val wrapper    = (new scalafx.scene.layout.VBox())
      scalafx.scene.layout.VBox.setVgrow(textarea, scalafx.scene.layout.Priority.Always)

      val e = new ScriptEngineManager().getEngineByName("scala")
      val interpreter = e.asInstanceOf[scala.tools.nsc.interpreter.IMain]
      interpreter.settings.usejavacp.value = true

      var code_changed_while_evaluating = false
      val evaluating = BooleanProperty(false)
      def read_and_refresh() : Unit = {
        val text = textarea.text.value
        import scala.concurrent._
        import ExecutionContext.Implicits.global
        if(evaluating.value) {
          code_changed_while_evaluating = true
        }else{
          evaluating.value = true
          val _ = Future {
            for(r <- eval(text)){
              Platform.runLater {
                wrapper.children.setAll(r)
              }}
            evaluating.value = false
            if(code_changed_while_evaluating) {
              code_changed_while_evaluating = false
              Platform.runLater {
                read_and_refresh()
              }
            }
          }
        }
      }

      def eval(to_eval: String) : Option[Node] = {
        try {
          val r = interpreter.compile(to_eval).eval()
          if(r.isInstanceOf[Node])
            Some(r.asInstanceOf[Node])
          else {
            Console.err.println("result is not Node")
            None
          }
        } catch {
          case ex: ScriptException => {
            Console.err.println(ex.getMessage())
            None
          }
          case ex: Throwable => {
            Console.err.println(ex.getMessage())
            None
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
      val indicator = new Text {
        text = "■"
        fill = Color.Red
      }
      evaluating onChange {
        Platform.runLater {
          if(evaluating.value)
            indicator.fill = Color.Green
          else
            indicator.fill = Color.Red
        }
      }
      val right = BVar[BBox]()
      val ret = (
        BFrame (titlize("Example: Pros/Cons Itemize Module"))
          * "We can define Pros/Cons list as an extension of Itemize."
          * (BColumns (0.5, 0.5)
                      (BBox
                         ("↓ frame will appear here↓")
                         (BVSpace (20 pt))
                         (wrapper)
                      )
                      (right := (BBox (textarea)
                                   (BColumns (0.3, 0.7, 0.1)
                                             (evalbutton)
                                             (live_check)
                                             (indicator)
                                   )))
          )
      )
      right.get.prefHeight <== (size_info.height - (50 pt))
      ret
    }
    def programmingEnvironment : BFrame = {
      val ret = (
        BFrame (titlize ("Environment: What Do We Need on The Fly?"))
          * "Live programming environment provides runtime information on the fly."
      )
      ret
    }
    def animation : BFrame = {
      val ret = (
        BFrame (titlize ("Environment: Animation Support"))
          * "This is just mock up, implemented in adhoc way."
      )
      ret
    }
    def guiAndText : BFrame = {
      val ret = (
        BFrame (titlize ("Idea: GUI to Text, Text to GUI"))
          * "This is just mock up, implemented in adhoc way."
      )
      ret
    }
    def testing : BFrame = {
      val ret = (
        BFrame (titlize ("Idea: \"Assert\" in Presentation?"))
          * "Context-dependent spell check?"
          * "Animation testing?"
          * "Live supports"
      )
      ret
    }
    def implementation : BFrame = {
      val ret = (
        BFrame (titlize ("Implementation: Shigure, the Prototype"))
      )
      ret
    }
    def conclusion : BFrame = {
      def genBallet(c: Color) = () => new Text{
        text = "■ "
        fill = Color.Green
      }
      val ret = (
        BFrame (titlize ("Conclusion and Future Work"))
          * (BBox (boxtitle("Conclusion", Color.Green))
                  (BColumns (0.025, 0.975)
                            ("")
                            (BBox ("I want modular/live programming environment for Presentation!")
                                  (BItemize (genBallet(Color.Green))
                                     - "Well-designed programming language Scala"
                                     - "EDSL for easy layout"
                                     - "Re-use existing tools for Scala")))
          )
          * BVSpace (30 pt)
          * (BBox (boxtitle("Future Work", Color.Green))
                  (BColumns (0.025, 0.975)
                            ("")
                            (BBox ("There are some work to make it \"usable.\"")
                                  (BItemize (genBallet(Color.Green))
                                     - "Animation framework"
                                     - "DSL for graphics"
                                     - "Complete bridge for Emacs"
                                     - "How to inject source code information to existing classes?"
                                     )))
          )
      )
      ret
    }

    val frames = Vector(
      titleFrame,
      aboutMe,
      caution,
      problems,
      approach1,
      approach2,
      example,
      programmingEnvironment,
      animation,
      guiAndText,
      testing,
      implementation,
      conclusion
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

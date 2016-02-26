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

import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress
import akka.actor.ActorSystem
import akka.event.Logging
import scala.language.postfixOps

import javax.script.ScriptEngineManager
import javax.script.ScriptException

import net.pushl.shigure.general._
import net.pushl.shigure.beamer._
import net.pushl.elrpc._

object Main extends JFXApp {
  val system = ActorSystem("repl-service")
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
      val itemize_head   = () => new Text {
        text = "◼ "
        fill = Color.Blue
      }
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
      val shiranui_url = getClass.getResource("/shiranui.png").toURI().toString
      val yukari_url   = getClass.getResource("/yukari.png").toURI().toString
      val coq_url      = getClass.getResource("/coq_live.png").toURI().toString
      val ret = (
        BFrame (titlize("About me"))
          * (BItemize
               - "Tomoki Imai (今井 朝貴)"
               - "Master course student at Tokyo Tech, Masuhara lab")
          * (BBox (boxtitle ("My works: Making programming fun and easier", Color.Green))
                  (BColumns (0.5, 0.5)
                            (BBox ("Test and live programming")
                                  (BImage(shiranui_url, size_info.width * 0.45))
                                  ("Live feedback for proof assistants")
                                  (BImage(coq_url, size_info.width * 0.45))
                            )
                            (BBox ("Real-world programming with VR")
                                  (BImage(yukari_url, size_info.width * 0.45))
                                  ("I'm also interested in...")
                                  (BItemize
                                     - "modular programming"
                                     - "functional programming"
                                     - "high-performance programming"
                                     - "machine-learning")
                                  ("↑ I think, they need better programming experience :)")

                            )
                  ))
      )
      ret
    }

    def caution : BFrame = {
      val ret = (
        BFrame (titlize(""))
          * BVSpace (50 pt)
          * "I will talk about work I just started."
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
                               - "Poor text editors"
                               - "Poor animation tool (low-level operation)"
                            ))
          )
          * BVSpace (8 pt)
          * (BColumns (picture_ratio, 1-picture_ratio)
                           (BImage(beamer_url, size_info.width * picture_ratio))
                    (BBox (boxtitle("  LaTeX Beamer", Color.Black))
                           (ProsConsItemize
                              + "Can use with my favorite text editors"
                              + "Can use version control like Git"
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
      import scala.concurrent._
      import ExecutionContext.Implicits.global
      val port = 42342
      val e = new ScriptEngineManager().getEngineByName("scala")
      val interpreter = e.asInstanceOf[scala.tools.nsc.interpreter.IMain]
      interpreter.settings.usejavacp.value = true
      val wrapper    = (new scalafx.scene.layout.VBox())
      val handler = new net.pushl.elrpc.DefaultHandler {
        var latestScene : Option[Any] = None
        // Animation class is only defined in Scala file...
        def goLine(s: Any, l: Long) : Vector[(Int, Int)] = {
          import scala.reflect.runtime.{universe => ru}
          def doSomething(s: Any, b: String) = {
            try {
              val m = ru.runtimeMirror(getClass.getClassLoader).reflect(s)
              val k = m.symbol.typeSignature.member(ru.newTermName(b))
              Some(m.reflectMethod(k.asMethod))
            } catch {
              case scala.ScalaReflectionException(_) => {
                None
              }
            }
          }
          var ret = Vector[(Int, Int)]()
          for(length  <- doSomething(s, "length");
              getItem <- doSomething(s, "apply")) {
            val anims = (0 until length().asInstanceOf[Int]).map((i: Int) => getItem(i))
            for(a <- anims;
                s <- doSomething(a, "start");
                e <- doSomething(a, "end");
                k <- doSomething(a, "isActive");
                c <- doSomething(a, "commit");
                r <- doSomething(a, "revert");
                g <- doSomething(a, "getState")){
              val h = s().asInstanceOf[Int]
              if(h <= l){
                ret = ret :+ ((h, g().asInstanceOf[Int]))
                if(!k().asInstanceOf[Boolean])
                  c()
              }else{
                if(k().asInstanceOf[Boolean])
                  r()
              }
            }
          }
          ret
        }
        override def methodsMap = Map(
          'evalfile -> ((uid: Long, args: SList) => {
                          args match {
                            case SList(List(SString(h))) => {
                              val e = Future {
                                import java.io._
                                val reader = new BufferedReader(
                                  new InputStreamReader(
                                    new FileInputStream(h)
                                  )
                                )
                                try {
                                  val comp = interpreter.compile(reader).eval()
                                  println(comp.toString)
                                  comp match {
                                    case (a, b: Node) => {
                                      Platform.runLater {
                                        wrapper.children.setAll(b)
                                      }
                                      latestScene = Some(a)
                                      Return(uid, SString(comp.toString))
                                    }
                                    case _ =>
                                      Return(uid, SString("Type error, you must return (a,b)"))
                                  }
                                } catch {
                                  case ex: ScriptException =>
                                    Return(uid, SString(ex.getMessage()))
                                  case ex: Throwable =>
                                    Return(uid, SString(ex.getMessage()))
                                }
                              }
                              List(e)
                            }
                            case _ => {
                              List(Future(Return(uid, SString("command error"))))
                            }
                          }
                        }),
          'moveline -> ((uid: Long, args: SList) => {
                      args match {
                        case SList(List(SInteger(line))) => {
                          println(latestScene)
                          println(line)
                          val active_lines =
                            latestScene match {
                              case Some(v) => goLine(v, line)
                              case None    => Vector[(Int,Int)]()
                            }
                          List(Future(Return(uid, SList(active_lines.toList.map({
                                                                                  case (a,b) => SList(List(SInteger(a), SInteger(b)))
                                                                                })))))
                        }
                        case _ =>
                          List(Future(Return(uid, SString("command error"))))
                      }
                    })
        )
      }
      val server = system.actorOf(
        Props(classOf[Server], port, handler), "belrpc-server")
      val ret = (
        BFrame (titlize ("Environment: Animation Support"))
          * (new QA()
               .question(boxtitle("Sync the cursor position and the animation.", Color.Green))
               .answer(BItemize
                         - "Reduce the gap between checking and writing animations.")
               .content(""))
          * (" Demo. (Just proof of concept, implemented in an ugly way.)")
          * (BColumns (0.5, 0.5)
                      (BBox
                         ("↓ frame will appear here↓")
                         (wrapper))
                      (BBox
                         (BVSpace (50 pt))
                         ("Emacs (TCP: " + port + ")")
                         (BVSpace (50 pt))
                      ))
      )
      ret
    }
    def testing1 : BFrame = {
      val sep = BVSpace (2 pt)
      val sep1 = BVSpace (1 pt)
      sep.style = "-fx-background-color: Black"
      sep1.style = sep.style.value

      val frame_source =
"""(BBox (titlize("this is title"))
      ("Following is itemize.")
      (BItemize
        - "item 1"
))"""
      val warn_source =
""""this is title" => "This is Title"
"Following is itemize"
  => "Following is itemize:"
"item 1" => "Item 1""""
      val ret = (
        BFrame (titlize ("Environment: \"Validate\" the Presentation?"))
          * " This is not implemented yet. Just idea."
          * (new QA()
               .question(boxtitle("Context-dependent syntax/spell check?", Color.Green))
               .answer("Spell/syntax check should know its own \"context.\"")
               .content(BItemize
                          - "Itemize can ignore the syntax a little?"
                          - "Is it title? If so, it should look like \"This is a Title \""
                          - "It define new keyword? We should make it bold or italic."
                          - "(Should we check statically?)"
             )
          )
          * sep
          * (BColumns (0.5, 0.5)
                      (BBox ("↓ rendered ↓")
                            (BBox (titlize("this is title"))
                                  ("Following is itemize.")
                                  (BItemize
                                     - "item 1"
                                  )))
                      (BBox ("↓ source code ↓")
                            (SourceCode.fromString(frame_source))
                            (sep1)
                            ("↓ syntax checker ↓")
                            (SourceCode.fromString(warn_source)))

          )
      )
      ret
    }
    def testing2 : BFrame = {
      val ret = (
        BFrame (titlize ("Environment: \"Validate\" the Presentation?"))
          * (new QA()
               .question(boxtitle("Q. How about tests for animation?", Color.Green))
               .answer("A. Generally speaking, it is hard to write tests by hands.")
               .content(" => \"Promoting\" or \"fixing\" can be good alternative.")
          )
      )
      ret
    }
    def implementation : BFrame = {
      val impl_url = getClass.getResource("/impl.png").toURI().toString
      val ret = (
        BFrame (titlize ("Implementation: Shigure, the Prototype"))
          * (BItemize
               - "ScalaFX (JavaFX) for GUI"
               - "Macro to inject sourcecode information"
               - "This slide have 700 LoC in Scala (most of it is example part)."
               - "Scala Emacs RPC for communicate (https://github.com/tomoki/Scala-elrpc)"
          )
          * (BColumns (0.1, 0.8, 0.1)
                      ("")
                      (BImage(impl_url, size_info.width * 0.8))
                      ("")
          )
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
                            (BBox ("Shigure: modular/live programming environment for Presentation")
                                  (BItemize (genBallet(Color.Green))
                                     - "Well-designed programming language Scala"
                                     - "EDSL, extensible and easy to write"
                                     - "Live programming for animation"
                                     - "Validate presentation")))
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
                                     - "Generate code from GUI"
                                     - "Inject sourcecode information to existing classes?"
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
      animation,
      testing1,
      testing2,
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

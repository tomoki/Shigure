val first_line = sourcecode.Line() // interpreter adds extra line.
import net.pushl.shigure.general._
import net.pushl.shigure.beamer._
import scalafx.scene.text.TextFlow
import scalafx.scene.text.Text

import scala.tools.nsc.interpreter.IMain
import javax.script.ScriptEngineManager
import scala.language.implicitConversions
import scalafx.Includes._
import scalafx.beans.property._
import scalafx.scene._

trait LInfo {
  def start: Int
  def end: Int
}

implicit def convertToTextFlow(s: String)
                              (implicit file: sourcecode.Line) : TextFlow with LInfo = {
  new TextFlow(new Text(s)) with LInfo {
    def start = file.value - first_line
    def end   = file.value - first_line
  }
}
class Itemize extends scalafx.scene.layout.VBox with LInfo{
  var start_v = 100000000
  var end_v   = -1
  def start = start_v
  def end   = end_v

  def -(n: Node with LInfo) : Itemize = {
    start_v = start_v min n.start
    end_v   = end_v   max n.end
    children.add((new TextFlow("■ ", n) with LInfo {
                   def start = n.start
                   def end   = n.end
                  }).asInstanceOf[Node])
    this
  }
  def apply(i: Int) : Node with LInfo = {
    jfxNode2sfx(children.get(i)).asInstanceOf[Node with LInfo]
  }
}
object Itemize {
  def -(n: Node with LInfo) : Itemize = {
    (new Itemize) - n
  }
}

trait Animation {
  def commit() : Unit
  def revert() : Unit
  def isActive() : Boolean
  var state = 1
  def getState() : Int = state
}

def ✔[A <: Animation](x: A) = {
  x.state = 2
  x
}

def ✘[A <: Animation](x: A) = {
  x.state = 0
  x
}

class StringAnimation(prop: StringProperty, to: String)(line: Int) extends Animation with LInfo{
  def start = line-first_line
  def end   = line-first_line
  def isActive() = is_active
  var is_active = false
  var saved : Option[String] = None
  def commit() : Unit =  {
    saved = Some(prop.value)
    prop.value = to
    is_active  = true
  }
  def revert() : Unit = {
    is_active = false
    saved match {
      case Some(v) => {
        prop.value = v
      }
      case None => {
        Console.err.println("Can't revert")
      }
    }
  }
}


implicit def stringTupleToAnimation(p: (StringProperty, String))(implicit line: sourcecode.Line)
    : StringAnimation = {
  new StringAnimation(p._1, p._2)(line.value)
}

class DoubleAnimation(prop: DoubleProperty, to: Double)(line: Int) extends Animation with LInfo{
  def start = line-first_line
  def end   = line-first_line
  var saved : Option[Double] = None
  var is_active = false
  def isActive() = is_active

  def commit() : Unit =  {
    saved = Some(prop.value)
    prop.value = to
    is_active  = true
  }
  def revert() : Unit = {
    is_active  = false
    saved match {
      case Some(v) => {
        prop.value = v
      }
      case None => {
        Console.err.println("Can't revert")
      }
    }
  }
}

implicit def doubleTupleToAnimation(p: (DoubleProperty, Double))(implicit line: sourcecode.Line)
    : DoubleAnimation = {
  new DoubleAnimation(p._1, p._2)(line.value)
}

// This is for second Emacs demo

val items = (Itemize
               - "line 1"
               - "line 2")


val scenes : Seq[Animation with LInfo] = Seq(
  (items(0).opacity, 1.0),
  (items(1).opacity, 0.2),
  (items(0).style, "-fx-background-color: Red")
)

(scenes, items)

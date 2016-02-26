val first_line = sourcecode.Line() // interpreter adds extra line.
import net.pushl.shigure.general._
import net.pushl.shigure.beamer._
import scalafx.scene.text.TextFlow
import scalafx.scene.text.Text

import scala.tools.nsc.interpreter.IMain
import javax.script.ScriptEngineManager
import scala.language.implicitConversions
import scalafx.scene.Node

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
    children.get(i).asInstanceOf[Node with LInfo]
  }
}
object Itemize {
  def -(n: Node with LInfo) : Itemize = {
    (new Itemize) - n
  }
}

val items = (Itemize
               - "line 1"
               - "line 2")
println(items.start)
println(items.end)

items(0).opacity -> 0
items

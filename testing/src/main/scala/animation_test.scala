import net.pushl.shigure.general._
import net.pushl.shigure.beamer._
import scalafx.scene.text.TextFlow
import scalafx.scene.text.Text

import scala.tools.nsc.interpreter.IMain
import javax.script.ScriptEngineManager

object Frame1 {
  val e = new ScriptEngineManager().getEngineByName("scala")
  val interpreter = e.asInstanceOf[IMain]
  interpreter.settings.usejavacp.value = true

  trait LInfo {
    def line: Int
  }
  import scala.language.implicitConversions
  implicit def convertToTextFlow(s: String)
                                (implicit file: sourcecode.File) : TextFlow with LInfo = {
    new TextFlow(new Text(s)) with LInfo {
      def line = file.value.line
    }
  }

  val a : (TextFlow with LInfo) = "aaaa"
  println(a.line)
}

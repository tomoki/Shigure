package net.pushl.shigure.beamer

import scalafx.scene._
import scalafx.scene.text._
import scalafx.scene.layout._
import scalafx.scene.paint._
import scalafx.scene.control._
import scalafx.geometry._
import scalafx.beans.property._

import scalafx.scene.layout.AnchorPane
import scalafx.animation._

import scalafx.Includes._
import net.pushl.shigure.general._

trait BeamerTheme {
  val itemize_head:   ()    => Node
  val enumerate_head: (Int) => Node
}

class BBox(title: Node) extends VBox {
  addItem(title)

  def addItem(n: Node) : BBox = {
    children.add(n)
    this
  }
  def apply(n: Node) : BBox = addItem(n)
  def apply(n: => Node) : BBox = addItem(n)
}

object BBox {
  def apply(title: Node) : BBox =
    new BBox(title)
}
class BItemize(ballet: () => Node) extends VBox {
  fillWidth = true
  def addItem(n: Node) : BItemize = {
    children.add(new TextFlow(ballet(), n))
    this
  }
  def -(n: Node) : BItemize = addItem(n)
  def apply(n: Int) : Node  = children(n)
}

object BItemize {
  def apply(ballet: () => Node) : BItemize =
    new BItemize(ballet)
  def -(n: Node)(implicit theme: BeamerTheme) : BItemize =
    apply(theme.itemize_head).addItem(n)
}

class BEnum(ballet: Int => Node) extends VBox {
  def addItem(n: Node) : BEnum = {
    children.add(new TextFlow(ballet(children.size()), n))
    this
  }
  def -(n: Node) = addItem(n)
  def apply(n: Int) : Node =
    children(n)
}

object BEnum {
  def apply(ballet: Int => Node) : BEnum =
    new BEnum(ballet)
  def -(n: Node)(implicit theme: BeamerTheme) : BEnum =
    apply(theme.enumerate_head).addItem(n)
}

class BFrame(title: Option[Node])(sizeinfo: FrameSizeInfo) extends VBox {
  minWidth  <== sizeinfo.width
  prefWidth <== sizeinfo.width
  maxWidth  <== sizeinfo.width

  for(t <- title)
    addItem(t)

  fillWidth = true
  def addItem(n: Node) : BFrame = {
    children.add(n)
    this
  }
  def *(n: Node) : BFrame = addItem(n)
}

object BFrame {
  def apply(title: Node)(implicit sizeinfo: FrameSizeInfo) : BFrame = {
    new BFrame(Some(title))(sizeinfo)
  }
  def *(n: Node)(implicit sizeinfo: FrameSizeInfo) : BFrame = {
    (new BFrame(None)(sizeinfo)) * n
  }
}


// FIXME: should be DPI aware.
class BVSpace(v: ReadOnlyDoubleProperty) extends HBox {
  val r = new Region()
  def setSize(a: Double) : Unit = {
    r.minHeight  = v.value
    r.maxHeight  = v.value
    r.prefHeight = v.value
  }

  setSize(v.value)
  v.onChange { setSize(v.value) }
  HBox.setHgrow(r, Priority.Always)
  children.setAll(r)
}

object BVSpace {
  def apply(v: ReadOnlyDoubleProperty) = new BVSpace(v)
}

class BColumns(per: Seq[Double]) extends GridPane {
  for(p <- per){
    val c = new ColumnConstraints()
    c.setPercentWidth(p * 100) // -> convert to %
    columnConstraints.add(c)
  }
  def apply(n: Node) : BColumns = {
    GridPane.setConstraints(n, children.size, 0)
    children.add(n)
    this
  }
}

object BColumns {
  def apply(per: Double*) =
    new BColumns(per)
}

class BVar[T <: Node](message: String) {
  var value : Option[T] = None
  def :=(n: T) : T = {
    this.value = Some(n)
    n
  }
  def get = value.get
  override def toString() : String = {
    "BVar(" + message + ")" + " -> " + value.toString
  }
}

object BVar {
  def apply[T <: Node](s: String, n: T) : BVar[T] = {
    val r = new BVar[T](s)
    r := n
    r
  }
  def apply[T <: Node](s: String) : BVar[T] =
    new BVar[T](s)
  def apply[T <: Node]() =
    new BVar[T]("Not specified")
}



import scala.language.implicitConversions
object BImplicits {
  implicit def convertToTextFlow(s: String) : TextFlow = {
    new TextFlow(new Text(s))
  }
  implicit def convertToT[T <: Node](v: BVar[T]) : T = {
    v.value match {
      case Some(a) => a
      case None    => sys.error("Can't refer: " + v.toString)
    }
  }
}

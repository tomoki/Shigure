package net.pushl.shigure.beamer

import scalafx.scene._
import scalafx.scene.text._
import scalafx.scene.layout._
import scalafx.scene.paint._
import scalafx.scene.control._
import scalafx.geometry._
import scalafx.beans.property._

import scalafx.scene.layout.AnchorPane



import net.pushl.shigure.general._

class BBox(title: Node) extends VBox {
  style = "-fx-background-color: pink"
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
class BItemize(ballet: => Node) extends VBox {
  fillWidth = true
  def addItem(n: Node) : BItemize = {
    children.add(new TextFlow(ballet, n))
    this
  }
  def -(n: Node) = addItem(n)
}

object BItemize {
  def apply(ballet: => Node) : BItemize =
    new BItemize(ballet)
  def -(n: Node) : BItemize =
    new BItemize({new Text("")}).addItem(n)
}

class BEnum(ballet: Int => Node) extends VBox {
  def addItem(n: Node) : BEnum = {
    children.add(new TextFlow(ballet(children.size()), n))
    this
  }
  def -(n: Node) = addItem(n)
}

object BEnum {
  def apply(ballet: Int => Node) : BEnum =
    new BEnum(ballet)
}

class BFrame(title: Option[Node])(sizeinfo: FrameSizeInfo) extends VBox {
  style = "-fx-background-color: yellow"
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
  v.onChange {
    r.minHeight  = v.value
    r.maxHeight  = v.value
    r.prefHeight = v.value
  }

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
  // TODO: maybe it does not perform covariant conversion
  // columnConstraints.setAll(per.map(
  //                            d => {
  //                              val c =  new ColumnConstraints()
  //                              c.setPercentWidth(d)
  //                              c
  //                            }))
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

class BVar(message: String) {
  var value : Option[Node] = None
  def :=(n: Node) : Node = {
    this.value = Some(n)
    n
  }
  def get = value.get
  override def toString() : String = {
    "BVar(" + message + ")" + " -> " + value.toString
  }
}

object BVar {
  def apply(s: String) : BVar =
    new BVar(s)
  def apply() =
    new BVar("Not specified")
}

import scala.language.implicitConversions
object BImplicits {
  implicit def convertToTextFlow(s: String) : TextFlow = {
    new TextFlow(new Text(s))
  }
}

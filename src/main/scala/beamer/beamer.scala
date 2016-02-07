package net.pushl.shigure.beamer

import scalafx.scene._
import scalafx.scene.text._
import scalafx.scene.layout._
import scalafx.scene.paint._
import scalafx.scene.control._
import scalafx.geometry._

import scalafx.scene.layout.AnchorPane

object BeamerConstants {
  val beamerBoxTitle = "beamer-box-title"
}

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

class BFrame(title: Node) extends VBox {
  // style = "-fx-background-color: yellow"
  addItem(title)
  def addItem(n: Node) : BFrame = {
    children.add(n)
    this
  }
  def apply(n: Node) : BFrame = addItem(n)
  def apply(n: => Node) : BFrame = addItem(n)
}

object BFrame {
  def apply(title: Node) : BFrame =
    new BFrame(title)
}

// FIXME: should be DPI aware.
class BVSpace(v: Double) extends HBox {
  val r = new Region()
  r.minHeight  = v
  r.maxHeight  = v
  r.prefHeight = v
  HBox.setHgrow(r, Priority.Always)
  children.setAll(r)
}

object BVSpace {
  def apply(v: Double) = new BVSpace(v)
}

class Columns(per: Seq[Double]) extends HBox {

}

import scala.language.implicitConversions
object BImplicits {
  implicit def convertToTextFlow(s: String) : TextFlow = {
    new TextFlow(new Text(s))
  }
}

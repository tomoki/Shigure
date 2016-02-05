package net.pushl.shigure.beamer

import scalafx.scene._
import scalafx.scene.text._
import scalafx.scene.layout._
import scalafx.scene.paint._
import scalafx.scene.control._

import scalafx.scene.layout.AnchorPane

object BeamerConstants {
  val beamerBoxTitle = "beamer-box-title"
}

class BeamerBox(title: Node, boxed: Node)
    extends VBox(Seq(title, {
                       title.getStyleClass().add(BeamerConstants.beamerBoxTitle)
                       AnchorPane.setAnchors(boxed, 10, 10, 10, 10)
                       new AnchorPane {
                         children = boxed
                       }
                     }): _*) {
  style = "-fx-background-color: pink"
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
}

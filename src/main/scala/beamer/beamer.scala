package scalpre.beamer

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

// JavaFX does not support duplicate method...
class BeamerItemize(ballet: () => Node)(children: Node*)
    extends VBox(children.map(
                   (c: Node) => {
                     new TextFlow(ballet(), c)
                   }): _*) {
}

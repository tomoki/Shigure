package scalpre

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.text._
import scalafx.scene.layout._
import scalafx.scene.paint._
import scalafx.scene._
import scalafx.stage.Screen
import scalafx.scene.control._
import scalafx.scene.transform._

import beamer._
import scalpre.util.Util


object Main extends JFXApp {
  val want_w_mm = 128
  val want_h_mm = 96

  // val want_w = 1920.0
  // val want_h = 1080.0
  stage = new PrimaryStage {
    title = "Scalpre"

    width  onChange scale
    height onChange scale

    // no need to take argument
    def scale() : Unit = {
      val want_w = Util.mmToPx(want_w_mm, Screen.primary.dpi)
      val want_h = Util.mmToPx(want_h_mm, Screen.primary.dpi)

      val scalefactor = (width() / want_w) min (height() / want_h)
      val rat = want_w / want_h
      val (aw, ah) =
        if(width() / height() > rat)
          (height() * rat, height())
        else
          (width(), width() / rat)

      AnchorPane.setTopAnchor(zoompane, (height()-ah)/2)
      AnchorPane.setBottomAnchor(zoompane, (height()-ah)/2)
      AnchorPane.setLeftAnchor(zoompane, (width()-aw)/2)
      AnchorPane.setRightAnchor(zoompane, (width()-aw)/2)

      val _ = group.getTransforms().setAll(
        Transform.scale(scalefactor,
                        scalefactor,
                        group.getLayoutBounds.getMinX,
                        group.getLayoutBounds.getMinY)
      )
    }
    val titlep = new TextFlow(
      new Text{
        text  = "The Extensible Type-Safe? Presentation Software"
        style = "-fx-font: normal 14pt 'Migu 1M'"
        fill  = Color.Blue
      }
    );
    val zoompane = new VBox()

    val group    = new Group(
      new VBox (
        titlep,
        new HBox(
        new BeamerBox(new Text{
                        text = "This is title"
                        style = "-fx-font: normal 11pt 'Migu 1P'"
                        fill  = Color.Green
                      },
                      new BeamerItemize(() => new Text("->"))(
                        new Text("おなかすいた"),
                        new Text("World"),
                        new Text("!")
                      )))))
    zoompane.children.add(group)

    AnchorPane.setAnchors(zoompane,100,0,0,0);
    val anchor = new AnchorPane {
      children = Seq(zoompane)
    }
    scene = new Scene {
      root = anchor
    }
  }
}

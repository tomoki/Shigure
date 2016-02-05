package net.pushl.shigure.general

import scalafx.scene.layout._
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

import scalafx.geometry.Insets 


import net.pushl.shigure.util.Util

class FixedSizePane (width_in_mm  : Double,
                     height_in_mm : Double) extends BorderPane {

  val group = new Group()
  this.setTop(group)
  BorderPane.setMargin(group, Insets(0, 0, 0, 0))
  style = "-fx-background-color: yellow"

  width  onChange { scale(width.value, height.value) }
  height onChange { scale(width.value, height.value) }

  def scale(to_width: Double, to_height: Double) : Unit = {
    group.resize(to_width, to_height)
    val wpx     = Util.mmToPx(width_in_mm , Screen.primary.dpi).toFloat
    val hpx     = Util.mmToPx(height_in_mm, Screen.primary.dpi).toFloat
    val sfactor = (to_width / wpx) min (to_height / hpx)

    val _ = group.getTransforms().setAll(
      Transform.scale(sfactor,
                      sfactor,
                      group.getLayoutBounds.getMinX,
                      group.getLayoutBounds.getMinY)
    )
  }
}

class FixedSizePaddingPane (width_in_mm  : Double,
                            height_in_mm : Double) extends AnchorPane {

  val zoompane = new FixedSizePane(width_in_mm, height_in_mm)
  AnchorPane.setAnchors(zoompane, 0, 0, 0, 0);
  children.add(zoompane)

  width  onChange { scale(width.value, height.value) }
  height onChange { scale(width.value, height.value) }

  def scale(to_width: Double, to_height: Double) : Unit = {
    val rat = width_in_mm / height_in_mm

    val (aw, ah) =
      if(to_width / to_height > rat)
        (to_height * rat, to_height)
      else
        (to_width, to_width / rat)

    zoompane.resize(aw, ah)
    AnchorPane.setTopAnchor(zoompane,    (to_height-ah)/2)
    AnchorPane.setBottomAnchor(zoompane, (to_height-ah)/2)
    AnchorPane.setLeftAnchor(zoompane,   (to_width-aw)/2)
    AnchorPane.setRightAnchor(zoompane,  (to_width-aw)/2)
  }
}

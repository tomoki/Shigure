package net.pushl.shigure.general

import scalafx.beans.property._

trait FrameSizeInfo {
  val width:  ReadOnlyDoubleProperty
  val height: ReadOnlyDoubleProperty
  val dpi:    ReadOnlyDoubleProperty
}

// implicits.

import scala.language.implicitConversions
object SizeImplicits {
  implicit class RichDoubleProperty(val self: ReadOnlyDoubleProperty) {
    def map(f: (Double) => Double) : ReadOnlyDoubleProperty = {
      val ret = DoubleProperty(f(self.value))
      self.onChange(ret.value = f(self.value))
      ret
    }
  }
  implicit class SizeDouble(val self: Double) extends AnyVal {
    def mm(implicit sizeinfo: FrameSizeInfo) =
      sizeinfo.dpi.map(d => Util.mmToPx(self, d))
    def px(implicit sizeinfo: FrameSizeInfo) =
      DoubleProperty(self)
    def pt(implicit sizeinfo: FrameSizeInfo) =
      sizeinfo.dpi.map(d => d * self / 72.0)
  }
}

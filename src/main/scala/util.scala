package net.pushl.shigure.util

object Util {
  // FIXME: We should check this is correct or not...
  def mmToPx(mm: Double, dpi: Double) : Double =
    mm / 25.4 * dpi
}

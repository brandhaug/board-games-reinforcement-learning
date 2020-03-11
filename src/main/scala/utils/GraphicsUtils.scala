package utils

import applications.hex.Window
import scalafx.scene.canvas.GraphicsContext

object GraphicsUtils {
  val offsetY: Double = Window.height * 0.25
  val offsetX: Double = 10
  def fillHexagon(gc: GraphicsContext, x: Int, y: Int, width: Double, height: Double): Unit = {
    gc.fillPolygon(
      Seq(
        (offsetX + (x * width) + (width * 0.5) + (y * (width * 0.5) + (x * 1)), offsetY + (y * (height * 0.75) + (y * 1))),
        (offsetX + (x * width) + width + (y * (width * 0.5) + (x * 1)), offsetY + (y * (height * 0.75) + (height * 0.25) + (y * 1))),
        (offsetX + (x * width) + width + (y * (width * 0.5) + (x * 1)), offsetY + (y * (height * 0.75) + (height * 0.75) + (y * 1))),
        (offsetX + (x * width) + (width * 0.5) + (y * (width / 2) + (x * 1)), offsetY + (y * (height * 0.75) + height + (y * 1))),
        (offsetX + (x * width + (y * (width * 0.5) + (x * 1))), offsetY + (y * (height * 0.75) + (height * 0.75) + (y * 1))),
        (offsetX + (x * width + (y * (width * 0.5) + (x * 1))), offsetY + (y * (height * 0.75) + (height * 0.25) + (y * 1)))
      )
    )
  }
}

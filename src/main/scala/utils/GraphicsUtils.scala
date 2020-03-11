package utils

import scalafx.scene.canvas.GraphicsContext

object GraphicsUtils {
  def fillHexagon(gc: GraphicsContext, x: Int, y: Int, width: Double, height: Double): Unit = {
    gc.fillPolygon(
      Seq(
        ((x * width) + (width * 0.5) + (y * (width * 0.5) + (x * 1)), y * (height * 0.75) + (y * 1)),
        ((x * width) + width + (y * (width * 0.5) + (x * 1)), y * (height * 0.75) + (height * 0.25) + (y * 1)),
        ((x * width) + width + (y * (width * 0.5) + (x * 1)), y * (height * 0.75) + (height * 0.75) + (y * 1)),
        ((x * width) + (width * 0.5) + (y * (width / 2) + (x * 1)), y * (height * 0.75) + height + (y * 1)),
        (x * width + (y * (width * 0.5) + (x * 1)), y * (height * 0.75) + (height * 0.75) + (y * 1)),
        (x * width + (y * (width * 0.5) + (x * 1)), y * (height * 0.75) + (height * 0.25) + (y * 1))
      )
    )
  }
}

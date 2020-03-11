package utils

import applications.hex.Window
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

object GraphicsUtils {
  val offsetY: Double = Window.height * 0.25
  val offsetX: Double = 10

  def fillHexagon(gc: GraphicsContext, x: Int, y: Int, width: Double, height: Double, maxX: Int, maxY: Int): Unit = {
    val topPoint         = (offsetX + (x * width) + (width * 0.5) + (y * (width * 0.5) + (x * 1)), offsetY + (y * (height * 0.75) + (y * 1)))
    val topRightPoint    = (offsetX + (x * width) + width + (y * (width * 0.5) + (x * 1)), offsetY + (y * (height * 0.75) + (height * 0.25) + (y * 1)))
    val bottomRightPoint = (offsetX + (x * width) + width + (y * (width * 0.5) + (x * 1)), offsetY + (y * (height * 0.75) + (height * 0.75) + (y * 1)))
    val bottomPoint      = (offsetX + (x * width) + (width * 0.5) + (y * (width / 2) + (x * 1)), offsetY + (y * (height * 0.75) + height + (y * 1)))
    val bottomLeftPoint  = (offsetX + (x * width + (y * (width * 0.5) + (x * 1))), offsetY + (y * (height * 0.75) + (height * 0.75) + (y * 1)))
    val topLeftPoint     = (offsetX + (x * width + (y * (width * 0.5) + (x * 1))), offsetY + (y * (height * 0.75) + (height * 0.25) + (y * 1)))

    gc.fillPolygon(
      Seq(
        topPoint,
        topRightPoint,
        bottomRightPoint,
        bottomPoint,
        bottomLeftPoint,
        topLeftPoint
      )
    )


    gc.setLineWidth(3)
    gc.setStroke(Color.Blue)

    if (x == 0) {
      gc.strokePolyline(
        Seq(
          bottomPoint,
          bottomLeftPoint,
          topLeftPoint
        )
      )
    } else if (x == maxX) {
      gc.strokePolyline(
        Seq(
          topPoint,
          topRightPoint,
          bottomRightPoint
        )
      )
    }

    gc.setStroke(Color.Red)
    if (y == 0) {
      gc.strokePolyline(
        Seq(
          topLeftPoint,
          topPoint,
          topRightPoint
        )
      )
    } else if (y == maxY) {
      gc.strokePolyline(
        Seq(
          bottomRightPoint,
          bottomPoint,
          bottomLeftPoint
        )
      )
    }
  }
}

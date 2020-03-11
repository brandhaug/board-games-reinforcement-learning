package applications.hex

import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafxml.core.macros.sfxml

@sfxml
class HexController(pane: Pane,
                            canvas: Canvas) {

  val gc: GraphicsContext = canvas.graphicsContext2D

  val size = 20.0
  val width: Double = Math.sqrt(3.0) * size
  val height: Double = 2 * size

  gc.setStroke(Color.Gold)
  gc.setLineWidth(2)

  gc.setFill(Color.Red)
  fillPolygon(0, 0)
  gc.setFill(Color.White)
  fillPolygon(0, 1)
  gc.setFill(Color.Khaki)
  fillPolygon(1, 0)
  gc.setFill(Color.Blue)
  fillPolygon(1, 1)
  gc.setFill(Color.Red)
  fillPolygon(0, 2)
  gc.setFill(Color.Green)
  fillPolygon(1, 2)
  gc.setFill(Color.OrangeRed)
  fillPolygon(2, 2)

  private def fillPolygon(x: Int, y: Int): Unit = {
    gc.fillPolygon(
      Seq(
        ((x * width) + (width * 0.5) + (y * (width / 2)) , y * (height * 0.75)),
        ((x * width) + width + (y * (width / 2)), y * (height * 0.75) + (height * 0.25)),
        ((x * width) + width + (y * (width / 2)), y * (height * 0.75) + (height * 0.75)),
        ((x * width) + (width * 0.5) + (y * (width / 2)), y * (height * 0.75) + height),
        (x * width + (y * (width / 2)), y * (height * 0.75) + (height * 0.75)),
        (x * width + (y * (width / 2)), y * (height * 0.75) + (height * 0.25))
      )
    )
  }
}

object Window {
  val width  = 800
  val height = 800
}

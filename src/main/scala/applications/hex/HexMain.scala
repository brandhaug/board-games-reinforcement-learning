package applications.hex

import java.io.IOException
import java.net.URL

import javafx.scene.Parent
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{FXMLView, NoDependencyResolver}

object HexMain extends JFXApp {
  val resource: URL = getClass.getResource("HexGUI.fxml")
  if (resource == null) {
    throw new IOException("Cannot load resource: HexGUI.fxml")
  }

  val root: Parent = FXMLView(resource, NoDependencyResolver)
  root.getStylesheets.add(getClass.getResource("../styles.css").toExternalForm)

  stage = new PrimaryStage() {
    title = "Hex example"
    scene = new Scene(root)
    resizable = false
  }
}


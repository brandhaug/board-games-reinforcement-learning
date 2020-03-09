package applications.actorcritic

import java.io.IOException
import java.net.URL

import javafx.scene.Parent
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{FXMLView, NoDependencyResolver}
import scalafx.Includes._

object SoloMain extends JFXApp {
  val resource: URL = getClass.getResource("SoloGUI.fxml")
  if (resource == null) {
    throw new IOException("Cannot load resource: SoloGUI.fxml")
  }

  val root: Parent = FXMLView(resource, NoDependencyResolver)
  root.getStylesheets.add(getClass.getResource("../styles.css").toExternalForm)

  stage = new PrimaryStage() {
    title = "RL - Peg Solitaire"
    scene = new Scene(root)
    resizable = false
  }
}

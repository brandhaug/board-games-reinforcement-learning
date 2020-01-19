package main

import java.io.File

import pegsolitaire.{Board, MapReader, PegSolitaire}
import reinforcementlearning.{Agent, State}
import scalafx.animation.AnimationTimer
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.{Button, ComboBox, Label}
import scalafx.scene.layout.{Pane, VBox}
import scalafxml.core.macros.sfxml

@sfxml
class Controller(val canvas: Canvas,
                 val vBoxMenu: VBox,
                 val comboBox: ComboBox[String],
                 val startButton: Button,
                 val resetButton: Button) {
  val environment: PegSolitaire.type = PegSolitaire
  val mapsDirectoryName = "boards"
  val files: List[File] = listFiles(mapsDirectoryName)
  val fileNames: List[String] = files.map(file => file.getName).sorted
  var selectedFileName: String = initializeFileSelector(fileNames)
  val gc: GraphicsContext = canvas.graphicsContext2D

  var animationTimer: AnimationTimer = _

  // States
  var paused = true

  initialize()

  def initialize(): Unit = {
    initializeGui()
    val board = initializeBoard()
    board.render(gc)
//    val initialState: State = environment.state(board)
//    val agent = Agent(initialState)
//    var previousState: State = initialState

    animationTimer = AnimationTimer(_ => {
      if (!paused) {
//        val action = agent.act(previousState)
//        val currentState: State = environment.step(previousState, action)

//        previousState = currentState
//        render(board, currentState)
      }
    })
    animationTimer.start()
  }

  def render(board: Board, state: State): Unit = { // (generation: Int, bestSchedule: Seq[OperationTimeSlot], bestMakeSpan: Int, machines: Seq[Machine]
    gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    board.render(gc)
    //    generationLabel.setText("Generation: " + generation)
    //    makeSpanLabel.setText("Make Span: " + bestMakeSpan)
  }

  def initializeGui(): Unit = {
    startButton.setText("Start")
    comboBox.setVisible(true)
    //    generationLabel.setText("Generation: -")
    //    makeSpanLabel.setText("Make span: -")
  }

  def initializeBoard(): Board = {
    val selectedFile = files.find(file => file.getName == selectedFileName).get
    MapReader.readFile(selectedFile)
  }

  def initializeFileSelector(fileNames: List[String]): String = {
    fileNames.foreach(fileName => comboBox += fileName)
    val selectedFileName = fileNames.head
    comboBox.getSelectionModel.select(fileNames.indexOf(selectedFileName))
    selectedFileName
  }

  def listFiles(directoryName: String): List[File] = {
    val path = getClass.getResource(directoryName)
    val folder = new File(path.getFile)
    if (folder.exists && folder.isDirectory) {
      folder.listFiles.toList
    } else {
      List[File]()
    }
  }

  def selectFile(): Unit = {
    selectedFileName = comboBox.getValue.toString
    reset()
  }

  def toggleStart(): Unit = {
    paused = !paused

    if (paused) {
      startButton.setText("Start")
    } else {
      startButton.setText("Pause")
    }
  }

  def reset(): Unit = {
    paused = true
    animationTimer.stop()
    gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    initialize()
  }
}

object Canvas {
  val width = 800
  val height = 800
}

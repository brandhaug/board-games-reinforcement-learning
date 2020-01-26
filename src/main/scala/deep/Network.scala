package deep

import environment.Environment
import environment.ActionType.ActionType
import main.Arguments
import org.platanios.tensorflow.api._
import org.platanios.tensorflow.api.core.types.UByte
import org.platanios.tensorflow.api.implicits.helpers.{OutputStructure, OutputToDataType, OutputToShape}
import org.platanios.tensorflow.api.learn.{ClipGradientsByGlobalNorm, Model}
import org.platanios.tensorflow.api.learn.layers.{Conv2D, Input, MaxPool, Mean, ReLU, ScalarSummary, SparseSoftmaxCrossEntropy}
import org.platanios.tensorflow.api.ops.NN.{SameConvPadding, ValidConvPadding}
import org.platanios.tensorflow.api.ops.Output
import org.platanios.tensorflow.api.ops.training.optimizers.GradientDescent

case class Network(initialEnvironment: Environment) {
  ???
//  val model: Model = {
//    val inputShape = Shape(-1, initialEnvironment.grid.length, initialEnvironment.grid.head.length)
//    val input      = Input(FLOAT32, inputShape)
//    val layer =
//      Conv2D[Float]("Layer_0", Shape(64, 3, 3), 3, 3, ValidConvPadding) >>
//        ReLU[Float]("Layer_0/Activation") >>
//        MaxPool[Float]("Layer_1", Seq(2, 2), 2, 2, ValidConvPadding) >>
//        ReLU[Float]("Layer_1/Activation") >>
//        Conv2D[Float]("OutputLayer", Shape(1, 3, 3), 3, 3, ValidConvPadding) >>
//        ReLU[Float]("OutputLayer/Activation")
//    val loss =
//      SparseSoftmaxCrossEntropy[Float, Long, Float]("Loss/CrossEntropy") >>
//        Mean[Float]("Loss/Mean") >>
//        ScalarSummary[Float]("Loss/Summary", "Loss")
//    val optimizer = GradientDescent(learningRate = Arguments.actorLearningRate)
//    Model.simpleSupervised(
//      input = input,
//      trainInput = input,
//      layer = layer,
//      loss = loss,
//      optimizer = optimizer
//    )
//  }
}

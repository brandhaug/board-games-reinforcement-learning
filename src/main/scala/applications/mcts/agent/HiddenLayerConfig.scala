package applications.mcts.agent

import applications.mcts.agent.HiddenLayerType.HiddenLayerType
import org.nd4j.linalg.activations.Activation

case class HiddenLayerConfig(layerType: HiddenLayerType, dimension: Int, activation: Activation)

object HiddenLayerType extends Enumeration {
  type HiddenLayerType = Value
  val Dense: HiddenLayerType.Value         = Value(0)
  val Convolutional: HiddenLayerType.Value = Value(1)
}

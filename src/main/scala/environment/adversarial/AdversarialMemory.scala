package environment.adversarial

import applications.mcts.PlayerType.PlayerType
import base.Memory
import environment.{Action, Environment}

case class AdversarialMemory(environment: Environment, action: Action, nextEnvironment: Environment, playerType: PlayerType) extends Memory

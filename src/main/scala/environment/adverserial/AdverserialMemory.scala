package environment.adverserial

import applications.mcts.PlayerType.PlayerType
import environment.{Action, Environment, Memory}

case class AdverserialMemory (environment: Environment, action: Action, nextEnvironment: Environment, playerType: PlayerType) extends Memory

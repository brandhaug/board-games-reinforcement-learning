package environment.adversarial

import applications.mcts.PlayerType.PlayerType
import base.Memory
import environment.{Action, Environment}

case class ActionVisitMemory(environment: Environment, action: Action, nextEnvironment: Environment, visits: Int, playerType: PlayerType) extends Memory

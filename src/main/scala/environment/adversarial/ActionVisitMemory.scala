package environment.adversarial

import base.Memory
import environment.{Action, Environment}

case class ActionVisitMemory(environment: Environment, action: Action, nextEnvironment: Environment, visits: Int) extends Memory

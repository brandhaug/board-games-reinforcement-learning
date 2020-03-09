package environment.solo

import base.Memory
import environment.{Action, Environment}

case class SoloMemory(environment: Environment, action: Action, nextEnvironment: Environment) extends Memory

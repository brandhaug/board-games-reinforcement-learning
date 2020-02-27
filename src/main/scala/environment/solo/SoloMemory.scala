package environment.solo

import environment.{Action, Environment, Memory}

case class SoloMemory(environment: Environment, action: Action, nextEnvironment: Environment) extends Memory

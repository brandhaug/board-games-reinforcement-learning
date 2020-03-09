package base

import environment.{Action, Environment}

trait Memory {
  val environment: Environment
  val action: Action
  val nextEnvironment: Environment
}

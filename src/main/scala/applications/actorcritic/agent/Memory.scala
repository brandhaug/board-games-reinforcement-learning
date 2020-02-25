package applications.actorcritic.agent

import environment.{Action, Environment}

case class Memory (environment: Environment, action: Action, nextEnvironment: Environment)

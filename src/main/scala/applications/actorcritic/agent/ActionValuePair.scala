package applications.actorcritic.agent

import environment.Action

case class ActionValuePair(action: Action, value: Double = 0)

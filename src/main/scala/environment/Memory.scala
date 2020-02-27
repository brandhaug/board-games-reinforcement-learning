package environment

trait Memory {
  val environment: Environment
  val action: Action
  val nextEnvironment: Environment
}

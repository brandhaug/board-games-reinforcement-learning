package applications.mcts

object PlayerType extends Enumeration {
    type StartingPlayerType = Value

    // Actor-critic
    val Mixed: PlayerType.Value = Value(0)
    val Player1: PlayerType.Value = Value(1)
    val Player2: PlayerType.Value = Value(2)
}

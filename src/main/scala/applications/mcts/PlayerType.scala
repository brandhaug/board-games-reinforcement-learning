package applications.mcts

object PlayerType extends Enumeration {
    type PlayerType = Value

    // Actor-critic
    val Mixed: PlayerType.Value = Value(0)
    val Player1: PlayerType.Value = Value(1)
    val Player2: PlayerType.Value = Value(2)

    def getNextPlayerType(playerType: PlayerType): PlayerType = {
        playerType match {
            case PlayerType.Player1 => PlayerType.Player2
            case PlayerType.Player2 => PlayerType.Player1
        }
    }
}

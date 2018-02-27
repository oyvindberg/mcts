package javabin

interface Board {
    fun allPossibleMoves(player: Player, availablePositions: List<Position>): List<Board> {
        return availablePositions.map { withMove(player, it) }
    }
    val currentPlayer: Player
    fun withPlayer(player: Player): Board
    fun withMove(player: Player, p: Position): Board
    fun checkStatus(): Status
}

data class Position(val x: Int = 0, val y: Int = 0)

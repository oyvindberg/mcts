package javabin

sealed class Status {
    data class InProgress(val positions: List<Position>) : Status()
    object Draw : Status()
    data class Win(val player: Player) : Status()
}


package javabin

sealed class Player {
    abstract val opponent: Player

    object One : Player() {
        override val opponent = Two
    }
    object Two : Player() {
        override val opponent = One
    }
}

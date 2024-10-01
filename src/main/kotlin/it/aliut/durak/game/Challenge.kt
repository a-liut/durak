package it.aliut.durak.game

data class Challenge(val attack: Card, var defense: Card?) {
    override fun toString(): String = "($attack, $defense)"
}

package it.aliut.durak.game

data class Card(val suit: Suit, val rank: Rank) {
    fun greaterThan(other: Card): Boolean = rank.value > other.rank.value

    override fun toString(): String = "${rank.value}${suit.symbol}"
}

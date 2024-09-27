package it.aliut.durak.game

class Deck {
    private val availableCards = mutableListOf<Card>()

    init {
        for (suit in Suit.entries) {
            for (rank in Rank.entries) {
                availableCards.add(Card(suit, rank))
            }
        }
    }

    val size: Int
        get() = availableCards.size

    fun isEmpty(): Boolean = availableCards.isEmpty()

    fun drawCard(): Card =
        availableCards.random().also {
            availableCards.remove(it)
        }
}

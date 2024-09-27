package it.aliut.durak.game

class Table {
    val challenges: MutableSet<Challenge> = mutableSetOf()

    private val openChallenges: Set<Challenge>
        get() = challenges.filter { it.defense == null }.toSet()

    val cardsOnTable: Set<Card>
        get() =
            challenges
                .flatMap { listOf(it.attack, it.defense) }
                .filterNotNull()
                .toSet()

    fun attackerPlayableCards(playerHand: Set<Card>): Set<Card> {
        if (challenges.isEmpty()) {
            return playerHand
        }

        return playerHand
            .filter { playerCard ->
                challenges.any {
                        challenge ->
                    challenge.attack.rank == playerCard.rank ||
                        challenge.defense!!.rank == playerCard.rank
                }
            }
            .toSet()
    }

    fun defenderPlayableCards(
        playerHand: Set<Card>,
        mainSuit: Suit,
    ): Set<Card> {
        if (challenges.isEmpty()) {
            error("Table is empty: defender cannot play")
        }

        val openAttacks = openChallenges.map { it.attack }

        return playerHand.filter { playerCard ->
            openAttacks.any {
                playerCard.suit == it.suit && playerCard.greaterThan(it) ||
                    playerCard.suit == mainSuit && it.suit != mainSuit ||
                    playerCard.suit == mainSuit && playerCard.greaterThan(it)
            }
        }.toSet()
    }

    override fun toString(): String = "$challenges"
}

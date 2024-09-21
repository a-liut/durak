package org.aliut.durak.game.player

import org.aliut.durak.game.Card

sealed class Player(val name: String) {
    val hand = mutableSetOf<Card>()

    abstract suspend fun selectAttackCard(playableCards: List<Card>): Card

    suspend fun playAttackCard(playableCards: List<Card>): Card {
        val playedCard = selectAttackCard(playableCards)

        hand.remove(playedCard)

        return playedCard
    }

    abstract suspend fun selectDefenseCard(playableCards: List<Card>): Card?

    suspend fun playDefenseCard(playableCards: List<Card>): Card? {
        val playedCard = selectDefenseCard(playableCards)

        hand.remove(playedCard)

        return playedCard
    }

    abstract suspend fun sendMessage(message: String)

    override fun toString(): String = name
}

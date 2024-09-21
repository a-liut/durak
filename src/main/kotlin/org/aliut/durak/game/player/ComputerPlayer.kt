package org.aliut.durak.game.player

import org.aliut.durak.game.Card

const val DEFAULT_SKIP_PROBABILITY = 0.5f

class ComputerPlayer(name: String, private val skipProbability: Float = DEFAULT_SKIP_PROBABILITY) : Player(name) {
    override suspend fun selectAttackCard(playableCards: List<Card>): Card = playableCards.random()

    override suspend fun selectDefenseCard(playableCards: List<Card>): Card? {
        val shallSkip = Math.random() < skipProbability
        if (shallSkip) {
            return null
        }

        return playableCards.random()
    }

    override suspend fun sendMessage(message: String) {
        println(message)
    }
}

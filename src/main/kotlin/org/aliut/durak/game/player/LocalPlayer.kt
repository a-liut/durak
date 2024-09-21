package org.aliut.durak.game.player

import org.aliut.durak.InputUtils
import org.aliut.durak.game.Card

class LocalPlayer(name: String) : Player(name) {
    override suspend fun selectAttackCard(playableCards: List<Card>): Card {
        sendMessage("Playable cards: $playableCards (hand: $hand)")

        val index =
            InputUtils.readValidInt(
                range = playableCards.indices,
                prompt = "Enter the index of the card you want to play:",
                errorMessage = "Invalid index. Please try again.",
            )

        return playableCards[index]
    }

    override suspend fun selectDefenseCard(playableCards: List<Card>): Card? {
        sendMessage("Playable cards: $playableCards (hand: $hand)")

        val index =
            InputUtils.readValidInt(
                range = -1..<playableCards.size,
                prompt = "Enter the index of the card you want to play or -1 to skip:",
                errorMessage = "Invalid index. Please try again.",
            )

        if (index == -1) {
            return null
        }

        return playableCards[index]
    }

    override suspend fun sendMessage(message: String) {
        println(message)
    }
}

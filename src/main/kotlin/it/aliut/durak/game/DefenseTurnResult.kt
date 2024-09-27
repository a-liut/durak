package it.aliut.durak.game

import it.aliut.durak.game.Durak.RoundResult

sealed class DefenseTurnResult {
    object NoDefense : DefenseTurnResult() {
        override fun toRoundResult(): RoundResult = RoundResult.NoDefense
    }

    object PlayerWon : DefenseTurnResult() {
        override fun toRoundResult(): RoundResult = RoundResult.PlayerWon
    }

    data class CardPlayed(val card: Card) : DefenseTurnResult() {
        override fun toRoundResult(): RoundResult = RoundResult.Continue
    }

    abstract fun toRoundResult(): RoundResult
}

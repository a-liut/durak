package it.aliut.durak.game

import it.aliut.durak.game.Durak.RoundResult
import it.aliut.durak.game.player.Player

sealed class DefenseTurnResult {
    object NoDefense : DefenseTurnResult() {
        override fun toRoundResult(): RoundResult = RoundResult.NoDefense
    }

    data class PlayerWon(val player: Player) : DefenseTurnResult() {
        override fun toRoundResult(): RoundResult = RoundResult.PlayerWon(player)
    }

    data class CardPlayed(val card: Card) : DefenseTurnResult() {
        override fun toRoundResult(): RoundResult = RoundResult.Continue
    }

    abstract fun toRoundResult(): RoundResult
}

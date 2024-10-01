package it.aliut.durak.game

import it.aliut.durak.game.Durak.RoundResult
import it.aliut.durak.game.player.Player

sealed class AttackTurnResult {
    object NoAttack : AttackTurnResult() {
        override fun toRoundResult(): RoundResult = RoundResult.NoAttack
    }

    data class PlayerWon(val player: Player) : AttackTurnResult() {
        override fun toRoundResult(): RoundResult = RoundResult.PlayerWon(player)
    }

    data class CardPlayed(val card: Card) : AttackTurnResult() {
        override fun toRoundResult(): RoundResult = RoundResult.Continue

        override fun toString(): String = card.toString()
    }

    abstract fun toRoundResult(): RoundResult
}

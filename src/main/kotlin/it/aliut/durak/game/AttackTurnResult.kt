package it.aliut.durak.game

import it.aliut.durak.game.Durak.RoundResult

sealed class AttackTurnResult {
    object NoAttack : AttackTurnResult() {
        override fun toRoundResult(): RoundResult = RoundResult.NoAttack
    }

    object PlayerWon : AttackTurnResult() {
        override fun toRoundResult(): RoundResult = RoundResult.PlayerWon
    }

    data class CardPlayed(val card: Card) : AttackTurnResult() {
        override fun toRoundResult(): RoundResult = RoundResult.Continue
    }

    abstract fun toRoundResult(): RoundResult
}

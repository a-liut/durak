package it.aliut.durak.game

import it.aliut.durak.game.player.Player
import kotlinx.coroutines.coroutineScope

const val PLAYER_HAND_SIZE = 6

class Durak(
    private val players: List<Player>,
) {
    val deck = Deck()
    private val table: Table = Table()
    private var mainCard: Card? = deck.drawCard()
    private val mainSuit: Suit = mainCard!!.suit

    private var roundCount: Int = 1
    private var currentDefenderIndex = 0

    private fun attack(card: Card): Challenge =
        Challenge(card, null).also {
            table.challenges.add(it)
        }

    private fun defend(
        challenge: Challenge,
        defense: Card,
    ) {
        challenge.defense = defense
    }

    private fun refillHand(player: Player) {
        if (deck.isEmpty()) {
            return
        }

        while (!deck.isEmpty() && player.hand.size < PLAYER_HAND_SIZE) {
            player.hand.add(deck.drawCard())
        }

        if (mainCard != null && player.hand.size < PLAYER_HAND_SIZE) {
            player.hand.add(mainCard!!)
            mainCard = null
        }
    }

    private fun nextTurn() {
        currentDefenderIndex = (currentDefenderIndex + 1) % players.size
    }

    private val previousPlayer: Player
        get() = players[(players.size + currentDefenderIndex - 1) % players.size]

    private val nextPlayer: Player
        get() = players[(currentDefenderIndex + 1) % players.size]

    private val attackers: List<Player>
        get() = listOf(nextPlayer, previousPlayer).distinct()

    private val defender: Player
        get() = players[currentDefenderIndex]

    suspend fun start() =
        coroutineScope {
            players.forEach {
                it.sendMessage("Game started")
                it.sendMessage("Players: ${players.joinToString { it.name }}")
                it.sendMessage("Main card: $mainCard")
                it.sendMessage("Main suit: $mainSuit")
            }

            for (player in players) {
                refillHand(player)
            }

            var roundResult: RoundResult
            do {
                roundResult = round()
                roundCount++
            } while (roundResult !is RoundResult.PlayerWon)

            val winner = roundResult.player
            players.forEach { it.sendMessage("Winner: ${winner.name}") }
        }

    private suspend fun round(): RoundResult {
        players.forEach { player ->
            player.sendMessage("--- Round $roundCount ---")
            player.sendMessage("Hand: ${player.hand}")

            val message =
                if (player == defender) {
                    "⛨ You are the defender!"
                } else {
                    "$defender is the defender."
                }

            player.sendMessage(message)
        }

        val roundResult = playRound(defender, attackers)

        if (roundResult is RoundResult.PlayerWon) {
            return roundResult
        }

        if (roundResult is RoundResult.NoDefense) {
            // defender takes all cards on the table
            defender.hand.addAll(table.cardsOnTable)
        }

        // prepare next turn
        table.challenges.clear()

        for (attacker in attackers) {
            refillHand(attacker)
        }

        refillHand(defender)

        nextTurn()
        if (roundResult is RoundResult.NoDefense) {
            // skip the defender's attack turn
            nextTurn()
        }

        return RoundResult.Continue
    }

    private suspend fun playRound(
        defender: Player,
        attackers: List<Player>,
    ): RoundResult {
        var roundResult: RoundResult
        var currentAttackerIndex = 0

        do {
            val attacker = attackers[currentAttackerIndex]

            do {
                roundResult = playChallenge(defender, attacker)
            } while (roundResult is RoundResult.Continue)

            if (roundResult is RoundResult.NoAttack) {
                currentAttackerIndex++
            }

            val isStopResult = roundResult in listOf(RoundResult.NoAttack, RoundResult.Continue)
            val lastAttacker = currentAttackerIndex < attackers.size
        } while (!isStopResult && !lastAttacker)

        return roundResult
    }

    private suspend fun playChallenge(
        defender: Player,
        attacker: Player,
    ): RoundResult =
        coroutineScope {
            players.forEach { player ->
                player.sendMessage("Table: $table")
                player.sendMessage("Deck: ${deck.size}")

                val message =
                    if (player == attacker) {
                        "⚔ You are the attacker!"
                    } else {
                        "$attacker is the attacker"
                    }

                player.sendMessage(message)
            }

            val playedAttack = playAttackerTurn(attacker)
            val playedAttackCard =
                when (playedAttack) {
                    is AttackTurnResult.NoAttack, is AttackTurnResult.PlayerWon -> {
                        return@coroutineScope playedAttack.toRoundResult()
                    }
                    is AttackTurnResult.CardPlayed -> playedAttack.card
                }

            val currentChallenge = attack(playedAttackCard)

            players.forEach { player ->
                val message =
                    if (player == attacker) {
                        "You played $playedAttack"
                    } else {
                        "$attacker played $playedAttack"
                    }

                player.sendMessage(message)
            }

            val playedDefense = playDefenderTurn(defender)
            val playedDefenseCard =
                when (playedDefense) {
                    is DefenseTurnResult.NoDefense, is DefenseTurnResult.PlayerWon -> {
                        return@coroutineScope playedDefense.toRoundResult()
                    }
                    is DefenseTurnResult.CardPlayed -> playedDefense.card
                }

            defend(currentChallenge, playedDefenseCard)

            players.forEach { player ->
                val message =
                    if (player == defender) {
                        "You played $playedDefense"
                    } else {
                        "$defender played $playedDefense"
                    }

                player.sendMessage(message)
            }

            RoundResult.Continue
        }

    private suspend fun playAttackerTurn(attacker: Player): AttackTurnResult {
        val attackerPlayableCards = table.attackerPlayableCards(attacker.hand)

        // skip attack if attacker has no playable cards
        if (attackerPlayableCards.isEmpty()) {
            players.forEach { player ->
                val message =
                    if (player == attacker) {
                        "You have no playable cards!"
                    } else {
                        "$attacker has no playable cards!"
                    }

                player.sendMessage(message)
            }

            return AttackTurnResult.NoAttack
        }

        val playedCard = attacker.playAttackCard(attackerPlayableCards.toList())

        // an attacker can win after playing his attack
        return if (attacker.hand.isEmpty()) {
            AttackTurnResult.PlayerWon(attacker)
        } else {
            AttackTurnResult.CardPlayed(playedCard)
        }
    }

    private suspend fun playDefenderTurn(defender: Player): DefenseTurnResult {
        val defenderPlayableCards = table.defenderPlayableCards(defender.hand, mainSuit)

        if (defenderPlayableCards.isEmpty()) {
            players.forEach { player ->
                val message =
                    if (player == defender) {
                        "You have no playable cards!"
                    } else {
                        "$defender has no playable cards!"
                    }

                player.sendMessage(message)
            }

            return DefenseTurnResult.NoDefense
        }

        val defenderCard = defender.playDefenseCard(defenderPlayableCards.toList())

        return defenderCard
            ?.let { DefenseTurnResult.CardPlayed(it) }
            ?.let {
                // a defender can win after playing his attack
                if (defender.hand.isEmpty()) {
                    DefenseTurnResult.PlayerWon(defender)
                } else {
                    it
                }
            }
            ?: DefenseTurnResult.NoDefense
                .also { players.forEach { it.sendMessage("$defender did not defend!") } }
    }

    sealed class RoundResult {
        object NoAttack : RoundResult()

        object NoDefense : RoundResult()

        data class PlayerWon(val player: Player) : RoundResult()

        object Continue : RoundResult()
    }

    companion object {
        const val PLAYER_COUNT_MIN = 2
        const val PLAYER_COUNT_MAX = 6
    }
}

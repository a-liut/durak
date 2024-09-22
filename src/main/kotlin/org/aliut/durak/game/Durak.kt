package org.aliut.durak.game

import kotlinx.coroutines.coroutineScope
import org.aliut.durak.game.player.Player

const val PLAYER_HAND_SIZE = 6

class Durak(
    private val players: List<Player>,
) {
    private val deck = Deck()
    private var mainCard: Card? = deck.drawCard()
    private val mainSuit: Suit = mainCard!!.suit

    private var currentDefenderIndex = 0

    private val table: MutableSet<Challenge> = mutableSetOf()

    fun winner(): Player = players.first { it.hand.isEmpty() }

    private fun attack(card: Card): Challenge =
        Challenge(card, null).also {
            table.add(it)
        }

    private fun defend(
        challenge: Challenge,
        defense: Card,
    ) {
        challenge.defense = defense
    }

    private fun clearTable() {
        table.clear()
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

    private fun previousPlayer(): Player = players[(players.size + currentDefenderIndex - 1) % players.size]

    private fun nextPlayer(): Player = players[(currentDefenderIndex + 1) % players.size]

    private fun attackers(): List<Player> = listOf(nextPlayer(), previousPlayer()).distinct()

    private fun defender(): Player = players[currentDefenderIndex]

    private fun openChallenges(): Set<Challenge> = table.filter { it.defense == null }.toSet()

    private fun attackerPlayableCards(playerHand: Set<Card>): Set<Card> {
        if (table.isEmpty()) {
            return playerHand
        }

        return playerHand
            .filter { playerCard ->
                table.any {
                        challenge ->
                    challenge.attack.rank == playerCard.rank ||
                        challenge.defense!!.rank == playerCard.rank
                }
            }
            .toSet()
    }

    private fun defenderPlayableCards(playerHand: Set<Card>): Set<Card> {
        if (table.isEmpty()) {
            throw IllegalStateException("Table is empty: defender cannot play")
        }

        val openAttacks = openChallenges().map { it.attack }

        return playerHand.filter { playerCard ->
            openAttacks.any {
                playerCard.suit == it.suit && playerCard.greaterThan(it) ||
                    playerCard.suit == mainSuit && it.suit != mainSuit ||
                    playerCard.suit == mainSuit && playerCard.greaterThan(it)
            }
        }.toSet()
    }

    private suspend fun sendMessageToAllPlayers(message: String) {
        players
            .forEach { it.sendMessage(message) }
    }

    suspend fun start() =
        coroutineScope {
            sendMessageToAllPlayers("Game started")
            sendMessageToAllPlayers("Players: ${players.joinToString { it.name }}")
            sendMessageToAllPlayers("Main card: $mainCard")
            sendMessageToAllPlayers("Main suit: $mainSuit")

            for (player in players) {
                refillHand(player)
            }

            while (true) {
                for (player in players) {
                    player.sendMessage("Hand: ${player.hand}")
                }

                val defender = defender()

                for (player in players) {
                    val message =
                        if (player == defender) {
                            "You are the defender!"
                        } else {
                            "$defender is the defender"
                        }

                    player.sendMessage(message)
                }

                val attackers = attackers()

                var noDefense = false
                var currentAttackerIndex = 0
                while (!noDefense && currentAttackerIndex < attackers.size) {
                    val attacker = attackers[currentAttackerIndex]

                    do {
                        val result = playRound(defender, attacker)

                        when (result) {
                            RoundResult.PlayerWon -> {
                                return@coroutineScope
                            }

                            RoundResult.NoDefense -> {
                                // defender takes all cards on the table
                                val cardsOnTheTable =
                                    table.flatMap { listOf(it.attack, it.defense) }.filterNotNull().toSet()
                                defender.hand.addAll(cardsOnTheTable)

                                noDefense = true
                            }

                            RoundResult.NoAttack -> {
                                currentAttackerIndex++
                            }

                            RoundResult.Continue -> {}
                        }
                    } while (result == RoundResult.Continue)
                }

                // prepare next turn
                clearTable()

                for (attacker in attackers) {
                    refillHand(attacker)
                }

                refillHand(defender)

                nextTurn()
                if (noDefense) {
                    // skip the defender's turn
                    nextTurn()
                }
            }
        }

    private suspend fun playRound(
        defender: Player,
        attacker: Player,
    ): RoundResult {
        sendMessageToAllPlayers("Table: $table")
        sendMessageToAllPlayers("Deck: ${deck.size}")

        for (player in players) {
            val message =
                if (player == attacker) {
                    "You are the attacker!"
                } else {
                    "$attacker is the attacker"
                }

            player.sendMessage(message)
        }

        val attackerPlayableCards = attackerPlayableCards(attacker.hand)

        // skip attack if attacker has no playable cards
        if (attackerPlayableCards.isEmpty()) {
            sendMessageToAllPlayers("attacker has no playable cards. Skipping attack")
            return RoundResult.NoAttack
        }

        val playedAttack = attacker.playAttackCard(attackerPlayableCards.toList())
        val currentChallenge = attack(playedAttack)

        // an attacker can win after playing his attack
        if (attacker.hand.isEmpty()) {
            return RoundResult.PlayerWon
        }

        sendMessageToAllPlayers("attacker played: $playedAttack")

        val defenderPlayableCards = defenderPlayableCards(defender.hand)

        if (defenderPlayableCards.isEmpty()) {
            sendMessageToAllPlayers("defender has no playable cards")

            return RoundResult.NoDefense
        }

        val playedDefense = defender.playDefenseCard(defenderPlayableCards.toList())
        if (playedDefense == null) {
            sendMessageToAllPlayers("defender skipped defense")

            return RoundResult.NoDefense
        }

        defend(currentChallenge, playedDefense)

        // a defender can win after playing his attack
        if (defender.hand.isEmpty()) {
            return RoundResult.PlayerWon
        }

        sendMessageToAllPlayers("defender played: $playedDefense")

        return RoundResult.Continue
    }

    enum class RoundResult {
        NoAttack,
        NoDefense,
        PlayerWon,
        Continue,
    }

    companion object {
        const val PLAYER_COUNT_MIN = 2
        const val PLAYER_COUNT_MAX = 6
    }
}

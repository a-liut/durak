package org.aliut.durak.game.player

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.aliut.durak.game.Card
import java.net.Socket

private val logger = KotlinLogging.logger { }

class RemotePlayer(name: String, private val socket: Socket) : Player(name) {
    private val reader = socket.getInputStream().bufferedReader()
    private val writer = socket.getOutputStream().bufferedWriter()

    fun dispose() {
        logger.debug { "Disposing remote player $name." }
        runCatching { reader.close() }
        runCatching { writer.close() }
        runCatching { socket.close() }
    }

    override suspend fun selectAttackCard(playableCards: List<Card>): Card =
        withContext(Dispatchers.IO) {
            sendMessage("Playable cards: $playableCards (hand: $hand)")

            var cardIndex: Int?

            do {
                cardIndex = reader.readLine().toIntOrNull()

                val invalidIndex = cardIndex == null || cardIndex !in playableCards.indices
                if (invalidIndex) {
                    sendMessage("Invalid card index. Please try again.")
                    logger.debug { "Invalid card index received: $cardIndex" }
                }
            } while (invalidIndex)

            playableCards[cardIndex!!]
        }

    override suspend fun selectDefenseCard(playableCards: List<Card>): Card? =
        withContext(Dispatchers.IO) {
            sendMessage("Playable cards: $playableCards (hand: $hand)")

            var cardIndex: Int?
            do {
                cardIndex = reader.readLine().toIntOrNull()

                val invalidIndex = cardIndex == null || (cardIndex !in playableCards.indices && cardIndex != -1)
                if (invalidIndex) {
                    sendMessage("Invalid card index. Please try again.")
                    logger.debug { "Invalid card index received: $cardIndex" }
                }
            } while (invalidIndex)

            if (cardIndex == -1) {
                return@withContext null
            }

            playableCards[cardIndex!!]
        }

    override suspend fun sendMessage(message: String) {
        withContext(Dispatchers.IO) {
            writer.write(message)
            writer.newLine()
            writer.flush()
        }
    }
}

package org.aliut.durak.gameroom

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.aliut.durak.game.player.Player
import org.aliut.durak.game.player.RemotePlayer
import java.net.ServerSocket

private val logger = KotlinLogging.logger { }

const val SERVER_SOCKET_PORT = 9999

class MultiplayerGameRoom(
    val playersCount: Int,
    initialPlayers: Collection<Player>,
    val socketPort: Int,
) {
    private val playersInRoom = mutableListOf<Player>()

    init {
        assert(initialPlayers.size <= playersCount)

        playersInRoom.addAll(initialPlayers)
    }

    val players: List<Player>
        get() = playersInRoom

    val isFull: Boolean
        get() = playersInRoom.size == playersCount

    fun addPlayer(player: Player) {
        if (isFull) {
            throw IllegalStateException("Maximum number of players reached")
        }

        playersInRoom.add(player)
    }

    suspend fun waitPlayers() =
        coroutineScope {
            if (isFull) {
                return@coroutineScope
            }

            withContext(Dispatchers.IO) {
                ServerSocket(socketPort).use { server ->
                    logger.debug { "Server running on port ${server.localPort}" }

                    while (!isFull) {
                        val client = server.accept()

                        logger.debug { "Client connected: ${client.inetAddress.hostAddress}" }

                        // Read the player's name
                        val name = client.getInputStream().bufferedReader().readLine()

                        val player = RemotePlayer(name, client)

                        addPlayer(player)

                        println("Player ${player.name} joined the game! players: ${players.size}/$playersCount")
                    }
                }
            }

            println("All players have joined the game!")

            logger.debug { "Starting the game." }
        }
}

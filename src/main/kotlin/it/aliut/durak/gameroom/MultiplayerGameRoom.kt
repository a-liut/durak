package it.aliut.durak.gameroom

import io.github.oshai.kotlinlogging.KotlinLogging
import it.aliut.durak.game.player.Player
import it.aliut.durak.game.player.RemotePlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.ServerSocket

private val logger = KotlinLogging.logger { }

const val DEFAULT_SERVER_SOCKET_PORT = 9999
const val DEFAULT_SERVER_SOCKET_HOST = "localhost"

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
            error("Maximum number of players reached")
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

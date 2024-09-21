package org.aliut.durak.runner

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.aliut.durak.game.player.LocalPlayer
import org.aliut.durak.game.player.Player
import org.aliut.durak.game.player.RemotePlayer
import java.net.ServerSocket

private val logger = KotlinLogging.logger { }

const val SERVER_SOCKET_PORT = 9999

data class MultiplayerGameConfig(
    val port: Int,
    val playersCount: Int,
    val localPlayer: LocalPlayer,
)

class MultiplayerGameRunner(private val config: MultiplayerGameConfig) : GameRunner() {
    override suspend fun initPlayers() =
        withContext(Dispatchers.Default) {
            println("Creating game room...")

            // TODO: Verify concurrent access
            val players: MutableList<Player> = mutableListOf(config.localPlayer)

            val remotePlayersToWait = config.playersCount - players.size

            println("Waiting for players to join...")

            val playerChannel = Channel<RemotePlayer>()

            val serverJob =
                launch {
                    startSocketServer(playerChannel, remotePlayersToWait)
                }

            val playerCollectorJob =
                launch {
                    for (player in playerChannel) {
                        players.add(player)

                        println("Player ${player.name} joined the game! players: ${players.size}/${config.playersCount}")
                    }

                    println("All players have joined the game!")
                }

            logger.debug { "Joining coroutines." }

            runCatching { serverJob.join() }
            runCatching { playerCollectorJob.join() }

            logger.debug { "Coroutines joined." }

            logger.debug { "Starting the game." }

            players
        }

    private suspend fun startSocketServer(
        playerChannel: Channel<RemotePlayer>,
        remotePlayersToAdd: Int,
    ) = withContext(Dispatchers.IO) {
        val server = ServerSocket(config.port)
        logger.debug { "Server running on port ${server.localPort}" }

        var joinedPlayers = 0
        while (joinedPlayers < remotePlayersToAdd) {
            val client = server.accept()

            logger.debug { "Client connected: ${client.inetAddress.hostAddress}" }

            // Read the player's name
            val name = client.getInputStream().bufferedReader().readLine()

            val player = RemotePlayer(name, client)

            playerChannel.send(player)

            joinedPlayers++
        }

        playerChannel.close()
    }
}

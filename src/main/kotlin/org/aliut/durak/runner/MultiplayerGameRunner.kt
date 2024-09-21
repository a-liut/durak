package org.aliut.durak.runner

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.aliut.durak.game.player.LocalPlayer
import org.aliut.durak.game.player.Player
import org.aliut.durak.gameroom.MultiplayerGameRoom

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

            val initialPlayers: MutableList<Player> = mutableListOf(config.localPlayer)

            val gameRoom = MultiplayerGameRoom(config.playersCount, initialPlayers, config.port)

            println("Waiting for players to join...")
            gameRoom.waitPlayers()

            gameRoom.players
        }
}

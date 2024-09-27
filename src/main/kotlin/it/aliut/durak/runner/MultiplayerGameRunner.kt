package it.aliut.durak.runner

import kotlinx.coroutines.coroutineScope
import it.aliut.durak.game.player.LocalPlayer
import it.aliut.durak.game.player.Player
import it.aliut.durak.gameroom.MultiplayerGameRoom

data class MultiplayerGameConfig(
    val port: Int,
    val playersCount: Int,
    val localPlayer: LocalPlayer,
)

class MultiplayerGameRunner(private val config: MultiplayerGameConfig) : GameRunner() {
    override suspend fun initPlayers() =
        coroutineScope {
            println("Creating game room...")

            val initialPlayers: MutableList<Player> = mutableListOf(config.localPlayer)

            val gameRoom = MultiplayerGameRoom(config.playersCount, initialPlayers, config.port)

            println("Waiting for players to join...")
            gameRoom.waitPlayers()

            gameRoom.players
        }
}

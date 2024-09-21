package org.aliut.durak.runner

import org.aliut.durak.game.player.Player

data class LocalGameConfig(
    val players: List<Player>,
)

class LocalGameRunner(private val config: LocalGameConfig) : GameRunner() {
    override suspend fun initPlayers(): List<Player> = config.players
}

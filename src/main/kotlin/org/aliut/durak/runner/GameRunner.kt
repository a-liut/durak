package org.aliut.durak.runner

import org.aliut.durak.game.Durak
import org.aliut.durak.game.player.Player
import org.aliut.durak.game.player.RemotePlayer

abstract class GameRunner {
    suspend fun startGame() {
        val players = initPlayers()

        val durak = Durak(players)

        try {
            durak.start()
        } finally {
            players.forEach(::disposePlayer)
        }

        println("Winner: ${durak.winner.name}")
    }

    abstract suspend fun initPlayers(): List<Player>

    private fun disposePlayer(player: Player) {
        if (player is RemotePlayer) {
            player.dispose()
        }
    }
}

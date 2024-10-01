package it.aliut.durak.runner

import it.aliut.durak.game.Durak
import it.aliut.durak.game.player.Player
import it.aliut.durak.game.player.RemotePlayer

abstract class GameRunner {
    suspend fun startGame() {
        val players = initPlayers()

        val durak = Durak(players)

        try {
            durak.start()
        } finally {
            players.forEach(::disposePlayer)
        }
    }

    abstract suspend fun initPlayers(): List<Player>

    private fun disposePlayer(player: Player) {
        if (player is RemotePlayer) {
            player.dispose()
        }
    }
}

package it.aliut.durak.menu

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import it.aliut.durak.game.Durak
import it.aliut.durak.game.player.LocalPlayer
import it.aliut.durak.gameroom.DEFAULT_SERVER_SOCKET_PORT
import it.aliut.durak.runner.MultiplayerGameConfig
import it.aliut.durak.runner.MultiplayerGameRunner

class StartMultiplayerGameCommand : SuspendingCliktCommand(name = "create") {
    private val port by option("-p", "--port", help = "Server port").int().default(DEFAULT_SERVER_SOCKET_PORT)
    private val localPlayerName by option("-pn", "--player-name", help = "Your name")
        .required()
    private val playersCount by option(
        "-pc",
        "--players-count",
        help = "Number of players (${Durak.PLAYER_COUNT_MIN}-${Durak.PLAYER_COUNT_MAX}",
    )
        .int()
        .required()
        .check("Players must be between ${Durak.PLAYER_COUNT_MIN} and ${Durak.PLAYER_COUNT_MAX}") {
            it in Durak.PLAYER_COUNT_MIN..Durak.PLAYER_COUNT_MAX
        }

    override fun help(context: Context): String = "Create a multiplayer game"

    override suspend fun run() {
        val localPlayer = LocalPlayer(localPlayerName)

        val config = MultiplayerGameConfig(port = port, playersCount = playersCount, localPlayer = localPlayer)

        val runner = MultiplayerGameRunner(config)
        runner.startGame()
    }
}

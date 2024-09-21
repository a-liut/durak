package org.aliut.durak.menu

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import org.aliut.durak.game.Durak
import org.aliut.durak.game.player.HumanLocalPlayer
import org.aliut.durak.runner.MultiplayerGameConfig
import org.aliut.durak.runner.MultiplayerGameRunner
import org.aliut.durak.runner.SERVER_SOCKET_PORT

class StartMultiplayerGameCommand : SuspendingCliktCommand(name = "create-multiplayer") {
    private val port by option(help = "Server port").int().default(SERVER_SOCKET_PORT)
    private val localPlayerName by option("-pn", "--player-name", help = "Your name")
        .required()
    private val playersCount by option(
        "-pc",
        "--players-count",
        help = "Number of players (${Durak.PLAYER_COUNT_MIN}-${Durak.PLAYER_COUNT_MAX}",
    )
        .int()
        .required()
        .check("Players must be between 2 and 6") { it in Durak.PLAYER_COUNT_MIN..Durak.PLAYER_COUNT_MAX }

    override fun help(context: Context): String = "Create a multiplayer game"

    override suspend fun run() {
        val localPlayer = HumanLocalPlayer(localPlayerName)

        val config = MultiplayerGameConfig(port = port, playersCount = playersCount, localPlayer = localPlayer)

        val runner = MultiplayerGameRunner(config)
        runner.startGame()
    }
}

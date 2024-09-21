package org.aliut.durak.menu

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import org.aliut.durak.InputUtils
import org.aliut.durak.game.Durak
import org.aliut.durak.game.player.ComputerPlayer
import org.aliut.durak.game.player.HumanLocalPlayer
import org.aliut.durak.game.player.Player
import org.aliut.durak.runner.LocalGameConfig
import org.aliut.durak.runner.LocalGameRunner

class StartLocalGameCommand : SuspendingCliktCommand(name = "local") {
    private val playersCount by option(
        "-pc",
        "--players-count",
        help = "Number of players (${Durak.PLAYER_COUNT_MIN}-${Durak.PLAYER_COUNT_MAX})",
    )
        .int()
        .required()
        .check("Players must be between ${Durak.PLAYER_COUNT_MIN} and ${Durak.PLAYER_COUNT_MAX}") {
            it in Durak.PLAYER_COUNT_MIN..Durak.PLAYER_COUNT_MAX
        }

    private val inputCpuCount by option(
        "-cc",
        "--cpu-count",
        help = "Number of CPU players",
    )
        .int()
        .required()
        .check("Players must be greater or equal than zero") { it >= 0 }

    override fun help(context: Context): String = "Start a local game"

    override suspend fun run() {
        val cpuCount = if (inputCpuCount > playersCount) playersCount else inputCpuCount
        val humanCount = playersCount - inputCpuCount

        val localPlayers = (1..humanCount).map { showCreateHumanLocalPlayer(it) }
        val cpuPlayers = (1..cpuCount).map { ComputerPlayer("CPU $it") }

        val config = LocalGameConfig(players = localPlayers + cpuPlayers)

        val runner = LocalGameRunner(config)
        runner.startGame()
    }

    private fun showCreateHumanLocalPlayer(playerIndex: Int): Player {
        val name =
            InputUtils.readString(
                prompt = "Enter the name of player $playerIndex:",
                errorMessage = "Invalid name. Please try again.",
            )

        return HumanLocalPlayer(name)
    }
}

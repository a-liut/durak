package org.aliut.durak.menu

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import org.aliut.durak.gameroom.SERVER_SOCKET_PORT
import org.aliut.durak.runner.JoinRemoteSessionService
import org.aliut.durak.runner.RemoteSessionConfig

class JoinMultiplayerGameCommand : SuspendingCliktCommand(name = "join-multiplayer") {
    private val host by option(help = "Host IP address").default("localhost")
    private val port by option(help = "Host port").int().default(SERVER_SOCKET_PORT)
    private val localPlayerName by option("-pn", "--player-name", help = "Your name")
        .required()

    override fun help(context: Context): String = "Join a multiplayer game"

    override suspend fun run() {
        val config = RemoteSessionConfig(remoteHost = host, remotePort = port, playerName = localPlayerName)

        val runner = JoinRemoteSessionService(config)
        runner.startGame()
    }
}

package it.aliut.durak.menu

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.subcommands

class DurakMenu : SuspendingCliktCommand(name = "durak") {
    override suspend fun run() = Unit

    companion object {
        fun create() =
            DurakMenu()
                .subcommands(
                    StartMultiplayerGameCommand(),
                    JoinMultiplayerGameCommand(),
                )
    }
}

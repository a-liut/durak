package it.aliut.durak

import com.github.ajalt.clikt.command.main
import it.aliut.durak.menu.DurakMenu

suspend fun main(args: Array<String>) = showBanner().also { DurakMenu.create().main(args) }

private fun showBanner() {
    val banner =
        """
            ██████╗ ██╗   ██╗██████╗  █████╗ ██╗  ██╗
            ██╔══██╗██║   ██║██╔══██╗██╔══██╗██║ ██╔╝
            ██║  ██║██║   ██║██████╔╝███████║█████╔╝ 
            ██║  ██║██║   ██║██╔══██╗██╔══██║██╔═██╗ 
            ██████╔╝╚██████╔╝██║  ██║██║  ██║██║  ██╗
            ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝
                                                     
        A classic russian game for terminal lovers.
        """.trimIndent()

    println(banner)
}

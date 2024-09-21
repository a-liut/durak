package org.aliut.durak

import com.github.ajalt.clikt.command.main
import org.aliut.durak.menu.DurakMenu

suspend fun main(args: Array<String>) = DurakMenu.create().main(args)

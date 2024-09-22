package org.aliut.durak.runner

import java.net.Socket

class RemotePlayerClient(name: String, val socket: Socket) {
    init {
        val writer = socket.getOutputStream().bufferedWriter()

        writer.write(name)
        writer.newLine()

        writer.flush()

        println("Connected to the game room! Waiting for the game to start...")
    }
}

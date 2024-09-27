package it.aliut.durak.runner

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.Socket

private val logger = KotlinLogging.logger { }

data class RemoteSessionConfig(
    val playerName: String,
    val remoteHost: String,
    val remotePort: Int,
)

class JoinRemoteSessionService(private val config: RemoteSessionConfig) {
    suspend fun startGame() =
        withContext(Dispatchers.IO) {
            val socket =
                try {
                    Socket(config.remoteHost, config.remotePort)
                } catch (e: IOException) {
                    println("Failed to connect to the remote host.")
                    logger.error(e) { "Failed to connect to the remote host." }
                    return@withContext
                }

            val player = RemotePlayerClient(config.playerName, socket)

            runRemotePlayer(player)
        }

    private suspend fun runRemotePlayer(player: RemotePlayerClient) =
        withContext(Dispatchers.IO) {
            val inputJob =
                launch {
                    player.socket.getInputStream()
                        .bufferedReader()
                        .use { reader ->
                            do {
                                val message =
                                    reader.readLine()?.also {
                                        println(it)
                                    }
                            } while (message != null)
                        }

                    // Close the socket
                    player.socket.close()
                    logger.debug { "Socket closed." }

                    println("Game has ended, press any key to exit.")
                }

            val outputJob =
                launch {
                    player.socket.getOutputStream()
                        .bufferedWriter()
                        .use { writer ->
                            while (!player.socket.isClosed) {
                                // TO FIX: With this code, the player can send messages at any time.
                                //  This is not good because the commands queue on the host, and it may be confusing
                                //  and create issues
                                val input =
                                    readln()
                                        .toIntOrNull()

                                if (!player.socket.isClosed) {
                                    if (input != null) {
                                        writer.write(input.toString())
                                        writer.newLine()
                                        writer.flush()
                                    } else {
                                        logger.info { "Invalid input. Please try again." }
                                    }
                                } else {
                                    logger.debug { "Player socket was closed." }
                                }
                            }
                        }
                }

            runCatching { inputJob.join() }
            runCatching { outputJob.join() }

            logger.debug { "Remote session ended." }
        }
}

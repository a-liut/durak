package it.aliut.durak

import java.io.InputStream

object InputUtils {
    fun readValidInt(
        inputStream: InputStream = System.`in`,
        range: IntRange? = null,
        prompt: String,
        errorMessage: String,
    ): Int {
        var input: Int?
        do {
            println(prompt)
            input = inputStream.bufferedReader().readLine().toIntOrNull()

            val invalidIndex = input == null || range?.contains(input) == false
            if (invalidIndex) {
                println(errorMessage)
            }
        } while (invalidIndex)

        return input
    }
}

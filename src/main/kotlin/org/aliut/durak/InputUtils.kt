package org.aliut.durak

import java.io.InputStream

class InputUtils {
    companion object {
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

                val invalidIndex = input == null || !(range?.contains(input) ?: true)
                if (invalidIndex) {
                    println(errorMessage)
                }
            } while (invalidIndex)

            return input!!
        }

        fun readString(
            inputStream: InputStream = System.`in`,
            prompt: String,
            errorMessage: String,
        ): String {
            var input: String
            do {
                println(prompt)
                input = inputStream.bufferedReader().readLine()

                val invalidInput = input.isBlank()
                if (invalidInput) {
                    println(errorMessage)
                }
            } while (invalidInput)

            return input
        }
    }
}

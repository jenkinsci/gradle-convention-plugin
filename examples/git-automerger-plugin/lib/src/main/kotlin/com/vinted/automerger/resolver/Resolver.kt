package com.vinted.automerger.resolver

import com.vinted.automerger.config.Resolution
import java.io.FileInputStream
import java.io.FileOutputStream

internal class Resolver(
    private val input: FileInputStream,
    private val output: FileOutputStream,
    private val mode: Resolution
) {
    fun resolve() {
        input.bufferedReader().use { input ->
            output.bufferedWriter().use { output ->
                val iterator = input.lineSequence().iterator()

                while (iterator.hasNext()) {
                    val line = iterator.next()
                    if (line.startsWith("<<<<<<<")) {
                        val resolved = resolveChunk(iterator)
                        output.write(resolved)
                    } else {
                        output.write(line)
                    }
                    output.newLine()
                }

                output.close()
            }
        }
    }

    private fun resolveChunk(lines: Iterator<String>): String {
        val new = lines.takeWhile { it != "=======" }
        val old = lines.takeWhile { !it.startsWith(">>>>>>>") }

        return mode.impl(new, old)
    }

    private fun Iterator<String>.takeWhile(predicate: (String) -> Boolean): String {
        val result = StringBuilder()
        while (hasNext()) {
            val line = next()
            if (predicate(line)) {
                result.appendLine(line)
            } else {
                break
            }
        }
        return result.removeSuffix("\n").toString()
    }
}

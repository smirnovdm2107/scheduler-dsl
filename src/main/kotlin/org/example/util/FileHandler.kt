package org.example.util

import java.io.FileWriter
import java.time.Instant

fun saveTimetable(stringEvents: String, name: String) {
    FileWriter("${name}${Instant.now()}".replace(':', '_')).use {file ->
        file.write(stringEvents)
    }
}
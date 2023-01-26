package util

import model.Scheduler
import java.io.FileWriter
import java.time.Instant

fun saveTimetable(stringEvents: String, name: String) {
    val file = FileWriter("${name}${Instant.now()}")
    file.write(stringEvents)
    file.flush()
    file.close()
}
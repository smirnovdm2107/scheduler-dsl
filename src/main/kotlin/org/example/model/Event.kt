package org.example.model

import java.util.Properties

@SchedulerDsl
class Event(val name: String) {
    val time: Time = Time()
    val properties: Properties = Properties()
    val description = Description()
}

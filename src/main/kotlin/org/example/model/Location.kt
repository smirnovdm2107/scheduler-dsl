package org.example.model

import java.util.Properties

@SchedulerDsl
class Location(name: String) {
    val description: Description = Description()
    val properties: Properties = Properties()
}

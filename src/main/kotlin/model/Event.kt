package model

import java.util.Properties

class Event(val name: String) {
    val time: Time = Time()
    val properties: Properties = Properties()
    val description = Description()
}
package model

import util.OpenRange
import util.Timestamp
import util.TimestampDTO

class Time {
    val timePoints = arrayListOf<Pair<WeekDay, OpenRange<TimestampDTO>>>()
    val apartFrom = arrayListOf<Int>()

    operator fun Timestamp.unaryPlus() = timePoints.add(this)
}
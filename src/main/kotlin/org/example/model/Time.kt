package org.example.model

import org.example.util.OpenRange
import org.example.util.Timestamp
import org.example.util.TimestampDTO

@SchedulerDsl
class Time {
    val timePoints = arrayListOf<Pair<WeekDay, OpenRange<TimestampDTO>>>()
    val apartFrom = arrayListOf<Int>()

    operator fun Timestamp.unaryPlus() = timePoints.add(this)
}

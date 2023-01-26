package model

import util.OpenRange
import util.TimestampDTO

class Time {
    val timePoints = arrayListOf<Pair<WeekDay, OpenRange<TimestampDTO>>>()
    val apartFrom = arrayListOf<Int>()
}
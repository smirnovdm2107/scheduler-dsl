package org.example.util

import org.example.model.WeekDay.*
import org.example.model.*

fun расписание(operations: Scheduler.() -> Unit) : Scheduler {
    return Scheduler().apply(operations)
}

fun Scheduler.событие(name: String, operations: Event.() -> Unit) : Event {
    val event = Event(name)
    events.add(event)
    event.apply(operations)
    return event
}

fun Event.время(operations: Time.() -> Unit) : Time {
    this.time.apply(operations)
    return this.time
}

fun Event.описание(operations: Description.() -> Unit) : Description {
    val description = this.description
    description.apply(operations)
    return description
}

open class TimeDTO(val hours: Int = 0, val minutes: Int = 0, val seconds: Int = 0) {
    open operator fun plus(time: TimeDTO) : TimeDTO = toPrettyFormat(
            this.hours + time.hours,
            this.minutes + time.minutes,
            this.seconds + time.seconds
        )


    open operator fun minus(time: TimeDTO) : TimeDTO = toPrettyFormat(
        this.hours - time.hours,
        this.minutes - time.minutes,
        this.seconds - time.seconds
    )

    private fun toPrettyFormat(hours: Int, minutes: Int, seconds: Int) : TimeDTO {
        val resultSeconds = seconds % 60
        val sumMinutes = minutes + seconds / 60
        val resultMinutes = sumMinutes % 60
        val resultHours = sumMinutes / 60 + hours
        return TimeDTO(resultHours, resultMinutes, resultSeconds)
    }

    override fun toString() : String = "$hours:$minutes:$seconds"

}

class TimestampDTO(hours: Int = 0,  minutes: Int = 0, seconds: Int = 0)
    : Comparable<TimestampDTO>, TimeDTO(hours, minutes, seconds){

    constructor(times: IntArray) : this(times[0], times[1], times[2])
    constructor(times: Array<Int>) : this(times[0], times[1], times[2])



    init {
        checkTimes(hours, minutes, seconds)
    }

    private fun checkTimes(hours: Int, minutes: Int, seconds: Int) {
        if (hours < 0 || hours > 23 ||
            minutes < 0 ||  minutes > 59 ||
            seconds < 0 || seconds > 59
        ) {
            throw IllegalArgumentException("you can't make timestamp with:\n$hours:$minutes:$seconds\n")
        }
    }

    override fun compareTo(other: TimestampDTO): Int {
        val hoursCompareTo = this.hours.compareTo(other.hours)
        if (hoursCompareTo == 0) {
            val minutesCompareTo = this.minutes.compareTo(other.minutes)
            if (minutesCompareTo == 0) {
                return  this.seconds.compareTo(other.seconds)
            }
            return minutesCompareTo
        }
        return hoursCompareTo
    }

    operator fun rangeTo(time: TimestampDTO) : OpenRange<TimestampDTO> = OpenRange(this, time)

    override operator fun plus(time: TimeDTO) : TimestampDTO = makeTimeOperation(time) {super.plus(time)}

    override operator fun minus(time: TimeDTO) : TimestampDTO = makeTimeOperation(time) {super.minus(time)}

    private fun makeTimeOperation(time: TimeDTO, f: (TimeDTO) -> TimeDTO) : TimestampDTO {
        val result = f(time)
        checkTimes(result.hours, result.minutes, result.seconds)
        return result as TimestampDTO
    }
}

typealias Timestamp = Pair<WeekDay, OpenRange<TimestampDTO>>


class OpenRange<T>(val left: T,val right: T) where T : Comparable<T> {
    init {
        if (left > right) {
            throw IllegalArgumentException(
                "left border of range ($left) must be less than right border of range ($right)")
        }
    }

    infix fun intersect(other: OpenRange<T>) : OpenRange<T>? {
        return when {
            this.right <= other.left || this.left >= other.right -> null
            else -> OpenRange(maxOf(this.left, other.left), minOf(this.right, other.right))
        }
    }

}
infix fun TimestampDTO.until(time: TimestampDTO) : OpenRange<TimestampDTO> = OpenRange(this, time)

fun Time.повторять(vararg days: Timestamp) {
    this.timePoints.addAll(days)
}
infix fun WeekDay.в(time: TimestampDTO) = Pair(this, time)
infix fun WeekDay.в(hours: Int) = Pair(this, TimestampDTO(hours))
infix fun WeekDay.в(timestampString: String) = Pair(this,
    TimestampDTO(timestampString.split(":").map{it.toInt()}.toTypedArray())
)
infix fun WeekDay.в(time: OpenRange<TimestampDTO>) = Pair(this, time)
infix fun WeekDay.в(time: IntRange) = Pair(this, OpenRange(TimestampDTO(time.first), TimestampDTO(time.last)))
infix fun List<WeekDay>.в(time: TimestampDTO) = this.map{ Pair(it, time)}
infix fun List<WeekDay>.в(hours: Int) = this.map{Pair(it, TimestampDTO(hours))}

infix fun Pair<WeekDay, TimestampDTO>.до(time: TimestampDTO) = Pair(this.first, this.second until time)
infix fun Pair<WeekDay, TimestampDTO>.до(time: Int) = this.до(TimestampDTO(time))
infix fun List<Pair<WeekDay, TimestampDTO>>.до(time: Int) =
    this.map{Pair(it.first, OpenRange(it.second, TimestampDTO(time)))}
fun Time.кроме(vararg days: Int) = apartFrom.addAll(days.toList())

fun Event.место(name: String, operations: Location.() -> Unit) : Location = Location(name).apply(operations)

fun Location.время_работы(range: IntRange) =
    this.время_работы(OpenRange(TimestampDTO(range.first), TimestampDTO(range.last)))
fun Location.время_работы(range: OpenRange<TimestampDTO>) {
    properties.set("время работы", range)
}
fun Location.описание(operations: Description.() -> Unit) : Description {
    return this.description.apply(operations)
}
fun Event.стоимость(value: Int) {
    properties.set("стоимость", value)
}

val каждый_день = WeekDay.values().toList()
val по_будням = каждый_день.filter { it != вс && it != сб}
val по_выходным = каждый_день.filter {it == вс || it == сб}

const val бесплатно = 0

operator fun Time.invoke(time: Timestamp) = timePoints.add(time)
fun Time.повторять(times: List<Timestamp>) = this.повторять(*times.toTypedArray())
fun Time.один_раз(vararg times: Timestamp) = timePoints.addAll(times)

package util

import model.*
import kotlin.math.min
import kotlin.properties.Delegates

fun расписание(operations: Scheduler.() -> Unit) : Scheduler = Scheduler().apply(operations)

fun Scheduler.событие(name: String, operations: Event.() -> Unit) : Event {
    val event = Event(name)
    events.add(event)
    Event(name).apply(operations)
    return event
}

fun Event.время(operations: Time.() -> Unit) : Time = Time().apply(operations)

fun Event.описание(operations: Description.() -> Unit) : Description = Description().apply(operations)

open class TimeDTO(val hours: Int = 0, val minutes: Int = 0, val seconds: Int = 0) {
    open operator fun plus(time: TimeDTO) : TimeDTO = TimeDTO(
        this.hours + time.hours,
              this.minutes + time.minutes,
                        this.seconds + time.seconds
        )

    open operator fun minus(time: TimeDTO) :TimeDTO = TimeDTO(
        this.hours + time.hours,
        this.minutes + time.minutes,
        this.seconds + time.seconds
    )

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
}
infix fun TimestampDTO.until(time: TimestampDTO) : OpenRange<TimestampDTO> = OpenRange(this, time)

fun Time.повторять(vararg days: Timestamp) = timePoints.addAll(days)
fun Time.повторять(vararg pairs: Pair<WeekDay, TimestampDTO>) =
    this.timePoints.addAll(pairs.map {Pair(it.first, OpenRange(it.second, it.second))})
infix fun WeekDay.в(time: TimestampDTO) = Pair(this, time)
infix fun WeekDay.в(hours: Int) = Pair(this, TimestampDTO(hours))
infix fun WeekDay.в(timestampString: String) = Pair(this,
    TimestampDTO(timestampString.split(":").map{it.toInt()}.toTypedArray()))
infix fun WeekDay.в(time: OpenRange<TimestampDTO>) = Pair(this, time)
infix fun WeekDay.в(time: IntRange) = Pair(this, OpenRange(TimestampDTO(time.first), TimestampDTO(time.last)))


infix fun Pair<WeekDay, TimestampDTO>.до(time: TimestampDTO) = Pair(this.first, this.second until time)
infix fun Pair<WeekDay, TimestampDTO>.до(time: Int) = this.до(TimestampDTO(time))

fun Time.кроме(vararg days: Int) = apartFrom.addAll(days.toList())



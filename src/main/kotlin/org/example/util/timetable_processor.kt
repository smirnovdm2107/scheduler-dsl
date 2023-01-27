package org.example.util

import org.example.model.Event
import org.example.model.Scheduler
import org.example.model.WeekDay
import java.time.LocalDate


fun Scheduler.process() {
    return SchedulerProcessor(this).process()
}

private class SchedulerProcessor(private val scheduler: Scheduler) {

    private val from: LocalDate
    private val to: LocalDate
    init {
        val now = LocalDate.now()
        val dayDiff = now.dayOfWeek.value - 1
        from = now.minusDays(dayDiff.toLong())
        to = from.plusDays(6)
    }



    fun process() {
        checkIntersections(this.scheduler.events)
        val stringEvents = joinEventsToString()
        saveTimetable(stringEvents, "TIMETABLE_V1")
    }

    fun joinEventsToString() : String {
        val sortedEvents = makeAllEventsTimeIntervals()
        val groupedByWeekOfDay = sortedEvents.groupBy { it.first.first }
        val sb: StringBuilder = StringBuilder()
        sb.appendStartScheduleString()
        for (day in groupedByWeekOfDay.keys.sorted()) {
            sb.append(addWeekStarter(day))
            groupedByWeekOfDay[day]?.forEach {
                sb.append(eventToString(it.first, it.second))
                    .append("\n")
                    .append("====\n")
            }
            sb.append(addWeekEnding(day))
        }
        return sb.toString()
    }

    fun addWeekStarter(day: WeekDay) : String {
        val sb = StringBuilder()
        sb.append("=======")
            .append(day)
            .append("-${from.plusDays(day.ordinal.toLong())}")
            .appendLine("=======")
        return sb.toString()
    }

    fun addWeekEnding(day: WeekDay) : String {
        return "=======================================\n"
    }

    fun makeAllEventsTimeIntervals() : List<Pair<Timestamp, Event>> {
        val resultList = ArrayList<Pair<Timestamp, Event>>()
        for (event in this.scheduler.events) {
            for (time in event.time.timePoints) {
                if (event.time.apartFrom.contains(time.first.ordinal + from.dayOfMonth)) {
                    continue
                }
                resultList.add(Pair(time, event))
            }
        }
        return resultList.sortedWith{ left, right ->
            val dayOfWeekComparison = left.first.first.compareTo(right.first.first)
            if (dayOfWeekComparison == 0) {
                left.first.second.left.compareTo(left.first.second.left)
            }
            dayOfWeekComparison
        }
    }

    fun StringBuilder.appendStartScheduleString() : StringBuilder {
        append("======_____======")
        append("Schedule for this week")
        appendLine("======_____======")
        return this
    }

    fun eventToString(time: Timestamp, event : Event) : String {
        val sb: StringBuilder = StringBuilder()
        sb.appendLine(event.name)
            .append(time.second.left)
            .append("-")
            .appendLine(time.second.right)
            .append("описание: ")
            .appendLine(event.description)
        for (property in event.properties) {
            sb.append(property.key)
                .append(": ")
                .appendLine(property.value)
        }
        return sb.toString()
    }

    fun checkIntersections(events: List<Event>) {
        val timestamps = ArrayList<Timestamp>()
        for (event in events) {
            for (time in event.time.timePoints) {
                if (event.time.apartFrom.contains(time.first.ordinal + from.dayOfMonth)) {
                    continue
                }
                for (processedTime in timestamps) {
                    if (processedTime.first != time.first) {
                        continue
                    }
                    if (processedTime.second.intersect(time.second) != null) {
                        throw IntersectionException("you must have not intersection in your schedule:\n " +
                                "[${processedTime.second.left}, ${processedTime.second.right}) intersect with " +
                                "[${time.second.left}, ${processedTime.second.right}) ")
                    }
                }
                timestamps.add(time)
            }
        }
    }


    class IntersectionException(message: String) : Exception(message)
}

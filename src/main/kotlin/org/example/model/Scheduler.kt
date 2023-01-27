package org.example.model

@SchedulerDsl
class Scheduler {
    val events = arrayListOf<Event>()

    @Deprecated(level = DeprecationLevel.ERROR, message = "invalid context")
    infix fun Scheduler.расписание(operations: Scheduler.() -> Unit) {}
}

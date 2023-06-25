package services

import entities.Course
import entities.Specialization

typealias Term = String
typealias TermSchedule = MutableList<Course>
typealias Schedule = MutableMap<Term, TermSchedule>

class Scheduler {
    fun generateSchedule(): Schedule {
        var schedule: Schedule = mutableMapOf()
        return schedule
    }
    fun updateSchedule(): Boolean {
        return true
    }

}

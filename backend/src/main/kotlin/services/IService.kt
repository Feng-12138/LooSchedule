package services

import entities.Communication
import entities.Course
import services.Schedule

//@Contract
interface IService {
    fun helloWorld(): String
    fun allCourses(): List<Course>
    fun allCommunications(): List<Communication>

    fun generateSchedule(): Schedule
}



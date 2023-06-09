package services

import entities.Communication
import entities.Course
import entities.Year

//@Contract
interface IService {
    fun helloWorld(): String
    fun allCourses(): List<Course>
    fun allCommunications(): List<Communication>

    fun generateSchedule(plan: AcademicPlan): MutableMap<String, MutableList<Course>>
}



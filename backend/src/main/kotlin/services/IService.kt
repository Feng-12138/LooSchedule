package services

import entities.Communication
import entities.Course

//@Contract
interface IService {
    fun helloWorld(): String
    fun allCourses(): List<Course>
    fun allCommunications(): List<Communication>

    fun generateSchedule(plan: AcademicPlan): Schedule

    fun validateSchedule(schedule: Schedule, degree: String): ScheduleValidationResult
}



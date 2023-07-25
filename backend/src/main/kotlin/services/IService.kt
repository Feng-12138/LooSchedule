package services

import entities.Communication
import entities.Course

//@Contract
interface IService {
    fun helloWorld(): String
    fun allCourses(): List<Course>
    fun allCommunications(): List<Communication>
    fun allPlanNames(): Plans

    suspend fun recommendCourses(position: String) :List<services.Course>

    fun generateSchedule(plan: AcademicPlan): Schedule

    fun validateSchedule(input: ScheduleValidator.ScheduleValidationInput): ScheduleValidator.ScheduleValidationOutput
}

data class Plans (
    val majors: List<String>,
    val minors: List<String>,
    val specializations: List<String>,
    val courses: List<String>,
)



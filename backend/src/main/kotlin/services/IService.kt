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

    fun generateSchedule(
        plan: AcademicPlan,
        recommendedCourses: MutableList<services.Course> = mutableListOf(),
    ): Schedule

    fun validateSchedule(input: ScheduleValidator.ScheduleValidationInput): ScheduleValidator.ScheduleValidationOutput
}

data class Plans (
    val majors: Set<String>,
    val minors: Set<String>,
    val specializations: Set<String>,
    val courses: List<Course>,
)



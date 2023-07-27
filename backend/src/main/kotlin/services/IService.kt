package services

import entities.Communication
import entities.Course
import services.utilities.Requirements

//@Contract
interface IService {
    fun helloWorld(): String
    fun allCourses(): List<Course>
    fun allCommunications(): List<Communication>
    fun allPlanNames(): Plans

    suspend fun recommendCourses(position: String) :List<services.utilities.Course>

    fun getRequirements(plan: AcademicPlan): Requirements

    fun validateSchedule(input: ScheduleValidator.ScheduleValidationInput): ScheduleValidator.ScheduleValidationOutput
}

data class Plans (
    val majors: Set<String>,
    val minors: Set<String>,
    val specializations: Set<String>,
    val courses: List<Course>,
)



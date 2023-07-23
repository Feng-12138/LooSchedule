package services

import entities.Course
import jakarta.inject.Inject
import repositories.ParsedPrereqData
import repositories.PrerequisiteRepo

typealias Schedule = MutableMap<String, MutableList<Course>>
typealias ScheduleValidationResult = Map<String, MutableSet<ScheduleValidator.ValidationResult>>

class ScheduleValidator {
    @Inject
    private lateinit var prerequisiteRepo: PrerequisiteRepo

    enum class ValidationResult {
        Success,
        TermUnavailable,
        NotMeetMinLvl,
        NotMeetPreReq,
        NotMeetCoReq,
        NotMeetAntiReq,
        NoSuchCourse,
        CommunicationCourseTooLate,
    }

    private fun checkCourseAvailability(term: String, course: Course): ValidationResult {
        return if (course.availability!!.contains(term)) {
            ValidationResult.Success
        } else {
            ValidationResult.TermUnavailable
        }
    }

    private fun checkCourseMinLevel(term: String, course: Course,
                                    prerequisite: MutableMap<String, ParsedPrereqData>): ValidationResult {
        val coursePrereq = prerequisite[course.courseID] ?: return ValidationResult.NoSuchCourse
        return if (coursePrereq.minimumLevel <= term) {
            ValidationResult.Success
        } else {
            ValidationResult.NotMeetMinLvl
        }
    }

    private fun checkCoursePrereq(course: Course, allCourses: List<String>,
                          prerequisite: MutableMap<String, ParsedPrereqData>): ValidationResult {
        val coursePrereq = prerequisite[course.courseID] ?: return ValidationResult.NoSuchCourse
        if (coursePrereq.courses.isEmpty() || coursePrereq.courses.all {it.isEmpty()}) return ValidationResult.Success
        for (requirement in coursePrereq.courses) {
            for (prereqCourse in requirement) {
                if (prereqCourse !in allCourses) {
                    return ValidationResult.NotMeetPreReq
                }
            }
            return ValidationResult.Success
        }

        println("Shouldn't run to here")
        return ValidationResult.Success
    }

    private fun checkCourseCoreq(course: Course, allCourses: List<String>,
                         prerequisite: MutableMap<String, ParsedPrereqData>): ValidationResult {
        return ValidationResult.Success
    }

    private fun checkCourseAntireq(course: Course, allCourses: List<String>,
                           prerequisite: MutableMap<String, ParsedPrereqData>): ValidationResult {
        return ValidationResult.Success
    }

    private fun checkList1CommunicationCourse(schedule: Schedule, degree: String): ValidationResult {
        return ValidationResult.Success
    }
    
    private fun checkOpenTo(course: Course, degree: String): ValidationResult {
        // Check if course satisfies not open to and only open to
        return ValidationResult.Success
    }
    
    fun validateSchedule(schedule: Schedule, degree: String): ScheduleValidationResult {
        val scheduleValidity = mutableMapOf<String, MutableSet<ValidationResult>>()
        val courseList: List<String> = schedule.values.flatten().map { it.courseID }
        val prerequisite = prerequisiteRepo.getParsedPrereqData(courseList)
        for ((term, scheduledCourses) in schedule) {
            // Need to check if the schedule has communication course scheduled in 1A or 1B depending on the degree
            for (course in scheduledCourses) {
                if (prerequisite[course.courseID] == null) {
                    scheduleValidity[course.courseID] = mutableSetOf(ValidationResult.NoSuchCourse)
                    continue
                }
                val result: MutableSet<ValidationResult> = mutableSetOf(
                    checkCourseAvailability(term, course),
                    checkCourseMinLevel(term, course, prerequisite),
                    checkCoursePrereq(course, courseList, prerequisite),
                    checkCourseCoreq(course, courseList, prerequisite),
                    checkCourseAntireq(course, courseList, prerequisite),
                    checkOpenTo(course, degree)
                )
                // Only keep the invalid reasons for that course
                result.removeIf { it == ValidationResult.Success }
                if (result.isNotEmpty()) scheduleValidity[course.courseID] = result
            }
        }

        return if (scheduleValidity.isEmpty()) {
            mapOf(
                "All" to mutableSetOf(ValidationResult.Success)
            )
        } else {
            scheduleValidity.toMap()
        }
    }
}
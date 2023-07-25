package services

import entities.Course
import jakarta.inject.Inject
import repositories.MajorRepo
import repositories.ParsedPrereqData
import repositories.PrerequisiteRepo

typealias Schedule = MutableMap<String, MutableList<Course>>
typealias ScheduleValidationResult = Map<String, MutableList<List<ScheduleValidator.ValidationResult>>>

class ScheduleValidator {
    @Inject
    private lateinit var prerequisiteRepo: PrerequisiteRepo

    @Inject
    private lateinit var majorRepo: MajorRepo

    data class ScheduleValidationInput (
        val schedule: Schedule = mutableMapOf(),
        val degree: String = "",
        val sequence: String = "Regular",
    )

    data class ScheduleValidationOutput (
        val validationDetails: ScheduleValidationResult = mapOf(),
        val overallResult: Boolean = false
    )

    enum class ValidationResult {
        Success,
        TermUnavailable,
        NotMeetMinLvl,
        NotMeetPreReq,
        NotMeetCoReq,
        NotMeetAntiReq,
        NoSuchCourse,
        CommunicationCourseTooLate,
        NoSuchMajor,
    }

    private val listOneCommunicationCourses = listOf("COMMST 100", "COMMST 223", "EMLS 101R", "EMLS 102R",
                                                   "EMLS 129R", "ENGL 129R", "ENGL 109")

    private fun checkCourseAvailability(termSeason: String, course: Course): ValidationResult {
        return if (course.availability!!.contains(termSeason)) {
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

    private fun checkCoursePrereq(course: Course, takenCourses: List<String>,
                          prerequisite: MutableMap<String, ParsedPrereqData>): ValidationResult {
        val coursePrereq = prerequisite[course.courseID] ?: return ValidationResult.NoSuchCourse
        if (coursePrereq.courses.isEmpty() || coursePrereq.courses.all {it.isEmpty()}) return ValidationResult.Success
        for (requirement in coursePrereq.courses) {
            var meetOneRequirement = true
            for (prereqCourse in requirement) {
                if (prereqCourse !in takenCourses) {
                    meetOneRequirement = false
                }
            }
            if (meetOneRequirement) return ValidationResult.Success
        }

        return ValidationResult.NotMeetPreReq
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
        val major: entities.Major = majorRepo.getMajorByName(degree) ?: return ValidationResult.NoSuchMajor
        // Double degree programs need to take list 1 communication course in 1A
        if (major.isDoubleDegree) {
            for (course in schedule["1A"]!!) {
                if (course.courseID in listOneCommunicationCourses) return ValidationResult.Success
            }
            return ValidationResult.CommunicationCourseTooLate
        }

        // For other degrees, need to take list 1 communication course before going into 2A
        val termKeys = listOf("1A", "1B", "WT1")
        val courses = termKeys.flatMap { schedule[it] ?: emptyList() }
        for (course in courses) {
            if (course.courseID in listOneCommunicationCourses) return ValidationResult.Success
        }
        return ValidationResult.CommunicationCourseTooLate
    }
    
    private fun checkOpenTo(course: Course, degree: String): ValidationResult {
        // Check if course satisfies not open to and only open to
        return ValidationResult.Success
    }
    
    fun validateSchedule(schedule: Schedule, degree: String, sequenceMap: Map<String, String>): ScheduleValidationOutput {
        val scheduleValidity = mutableMapOf<String, MutableList<List<ValidationResult>>>()
        var overallResult = true
        val courseList: List<String> = schedule.values.flatten().map { it.courseID }
        val takenSoFar: MutableList<String> = mutableListOf()
        val prerequisite = prerequisiteRepo.getParsedPrereqData(courseList)

        // First check communication courses
        // val commRes: ValidationResult = checkList1CommunicationCourse(schedule, degree)
        // if (commRes != ValidationResult.Success) scheduleValidity["Communication"] = mutableSetOf(commRes)

        // Check schedule validity
        for ((term, scheduledCourses) in schedule) {
            val termResult = mutableListOf<List<ValidationResult>>()
            for (course in scheduledCourses) {
                if (prerequisite[course.courseID] == null) {
                    termResult.add(listOf(ValidationResult.NoSuchCourse))
                    overallResult = false
                    continue
                }
                val courseResult: MutableSet<ValidationResult> = mutableSetOf(
                    checkCourseAvailability(sequenceMap[term]!!, course),
                    checkCourseMinLevel(term, course, prerequisite),
                    checkCoursePrereq(course, takenSoFar, prerequisite),
                    checkCourseCoreq(course, courseList, prerequisite),
                    checkCourseAntireq(course, courseList, prerequisite),
                    checkOpenTo(course, degree)
                )
                // Only keep the invalid reasons for that course
                courseResult.removeIf { it == ValidationResult.Success }
                if (courseResult.isNotEmpty()) overallResult = false
                termResult.add(courseResult.toList())
                takenSoFar.add(course.courseID)
            }
            scheduleValidity[term] = termResult
        }

        return ScheduleValidationOutput(scheduleValidity, overallResult)
    }
}
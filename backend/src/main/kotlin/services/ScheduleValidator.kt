package services

import entities.Course
import jakarta.inject.Inject
import repositories.MajorRepo
import repositories.ParsedPrereqData
import repositories.PrerequisiteRepo

typealias Schedule = MutableMap<String, MutableList<Course>>
typealias CourseValidationResult = Map<String, MutableList<List<ScheduleValidator.ValidationResult>>>
typealias DegreeValidationResult = List<ScheduleValidator.OverallValidationResult>

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
        val courseValidationResult: CourseValidationResult = mapOf(),
        val degreeValidationResult: DegreeValidationResult = listOf(),
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
    }

    enum class OverallValidationResult {
        Success,
        NoSuchMajor,
        CommunicationCourseTooLate,
        NotEnoughCourse,
        NotMeetDegreeRequirement,
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
                                    courseConstraints: MutableMap<String, ParsedPrereqData>): ValidationResult {
        val courseMinLvl = courseConstraints[course.courseID] ?: return ValidationResult.NoSuchCourse
        return if (courseMinLvl.minimumLevel <= term) {
            ValidationResult.Success
        } else {
            ValidationResult.NotMeetMinLvl
        }
    }

    private fun checkCoursePreReq(course: Course, takenCourses: List<String>,
                                  courseConstraints: MutableMap<String, ParsedPrereqData>): ValidationResult {
        val coursePreReq = courseConstraints[course.courseID] ?: return ValidationResult.NoSuchCourse
        if (coursePreReq.courses.isEmpty() || coursePreReq.courses.all {it.isEmpty()}) return ValidationResult.Success
        for (requirement in coursePreReq.courses) {
            var meetOneRequirement = true
            for (preReqCourse in requirement) {
                if (preReqCourse !in takenCourses) {
                    meetOneRequirement = false
                }
            }
            if (meetOneRequirement) return ValidationResult.Success
        }

        return ValidationResult.NotMeetPreReq
    }

    private fun checkCourseCoReq(course: Course, takenAndTakingCourses: List<String>,
                                 courseConstraints: MutableMap<String, ParsedPrereqData>): ValidationResult {
        val courseCoReq = courseConstraints[course.courseID] ?: return ValidationResult.NoSuchCourse
        if (courseCoReq.coreqCourses.isEmpty() || courseCoReq.coreqCourses.all {it.isEmpty()}) return ValidationResult.Success

        return ValidationResult.Success
    }

    private fun checkCourseAntiReq(course: Course, takenAndTakingCourses: List<String>,
                                   courseConstraints: MutableMap<String, ParsedPrereqData>): ValidationResult {
        val courseAntiReq = courseConstraints[course.courseID] ?: return ValidationResult.NoSuchCourse
        if (courseAntiReq.antireqCourses.isEmpty()) return ValidationResult.Success
        for (antiReqCourse in courseAntiReq.antireqCourses) {
            if (takenAndTakingCourses.contains(antiReqCourse)) return ValidationResult.NotMeetAntiReq
        }
        return ValidationResult.Success
    }

    private fun checkList1CommunicationCourse(schedule: Schedule, degree: String): OverallValidationResult {
        val major: entities.Major = majorRepo.getMajorByName(degree) ?: return OverallValidationResult.NoSuchMajor
        // Double degree programs need to take list 1 communication course in 1A
        if (major.isDoubleDegree) {
            for (course in schedule["1A"]!!) {
                if (course.courseID in listOneCommunicationCourses) return OverallValidationResult.Success
            }
            return OverallValidationResult.CommunicationCourseTooLate
        }

        // For other degrees, need to take list 1 communication course before going into 2A
        val termKeys = listOf("1A", "1B", "WT1")
        val courses = termKeys.flatMap { schedule[it] ?: emptyList() }
        for (course in courses) {
            if (course.courseID in listOneCommunicationCourses) return OverallValidationResult.Success
        }
        return OverallValidationResult.CommunicationCourseTooLate
    }
    
    private fun checkOpenTo(course: Course, degree: String): ValidationResult {
        // Check if course satisfies not open to and only open to
        return ValidationResult.Success
    }
    
    fun validateSchedule(schedule: Schedule, degree: String, sequenceMap: Map<String, String>): ScheduleValidationOutput {
        val courseValidity = mutableMapOf<String, MutableList<List<ValidationResult>>>()
        val degreeValidity = mutableListOf<OverallValidationResult>()
        var overallResult = true
        val courseList: List<String> = schedule.values.flatten().map { it.courseID }
        val takenSoFar: MutableList<String> = mutableListOf()
        val takenAndTakingSoFar: MutableList<String> = mutableListOf()
        val courseConstraints = prerequisiteRepo.getParsedPrereqData(courseList)

        // First check communication courses
         val commRes: OverallValidationResult = checkList1CommunicationCourse(schedule, degree)
         if (commRes != OverallValidationResult.Success) {
             degreeValidity.add(commRes)
             // Currently, not satisfying communication timeline is only a warning.
             // Will not result in schedule being invalid, but could change this later
         }

        // Check single course validity
        for ((term, scheduledCourses) in schedule) {
            takenAndTakingSoFar.addAll(scheduledCourses.map { it.courseID })
            val termResult = mutableListOf<List<ValidationResult>>()
            for (course in scheduledCourses) {
                if (courseConstraints[course.courseID] == null) {
                    termResult.add(listOf(ValidationResult.NoSuchCourse))
                    overallResult = false
                    continue
                }
                val courseResult: MutableSet<ValidationResult> = mutableSetOf(
                    checkCourseAvailability(sequenceMap[term]!!, course),
                    checkCourseMinLevel(term, course, courseConstraints),
                    checkCoursePreReq(course, takenSoFar, courseConstraints),
                    checkCourseCoReq(course, takenAndTakingSoFar, courseConstraints),
                    checkCourseAntiReq(course, takenAndTakingSoFar, courseConstraints),
                    checkOpenTo(course, degree)
                )
                // Only keep the invalid reasons for that course
                courseResult.removeIf { it == ValidationResult.Success }
                if (courseResult.isNotEmpty()) overallResult = false
                termResult.add(courseResult.toList())
                takenSoFar.add(course.courseID)
            }
            courseValidity[term] = termResult
        }

        // Check if degree requirements are satisfied

        return ScheduleValidationOutput(courseValidity, degreeValidity, overallResult)
    }
}
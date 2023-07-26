package services

import entities.Course
import entities.Major
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
        val academicPlan: AcademicPlan = AcademicPlan()
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
        NotOpenTo,
        NoSuchCourse,
    }

    enum class OverallValidationResult {
        Success,
        InvalidMajor,
        CommunicationCourseTooLate,
        NotEnoughCourse,
        NotMeetMandatoryCourses,
        NotMeetOptionalCourses,
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

        for (requirement in courseCoReq.courses) {
            var meetOneRequirement = true
            for (coReqCourse in requirement) {
                if (coReqCourse !in takenAndTakingCourses) {
                    meetOneRequirement = false
                }
            }
            if (meetOneRequirement) return ValidationResult.Success
        }

        return ValidationResult.NotMeetCoReq
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

    private fun checkList1CommunicationCourse(schedule: Schedule, majorNames: List<String>): OverallValidationResult {
        val majors: List<Major> = majorNames.mapNotNull { majorName ->
            majorRepo.getMajorByName(majorName)
        }
        if (majorNames.size != majors.size) return OverallValidationResult.InvalidMajor

        // Double degree programs need to take list 1 communication course in 1A
        if (majors.any { it.isDoubleDegree }) {
            for (course in schedule["1A"]!!) {
                if (course.courseID in listOneCommunicationCourses) return OverallValidationResult.Success
            }
            return OverallValidationResult.CommunicationCourseTooLate
        }

        // For other math programs, need to take list 1 communication course before going into 2A
        val termKeys = listOf("1A", "1B", "WT1")
        val courses = termKeys.flatMap { schedule[it] ?: emptyList() }
        for (course in courses) {
            if (course.courseID in listOneCommunicationCourses) return OverallValidationResult.Success
        }
        return OverallValidationResult.CommunicationCourseTooLate
    }
    
    private fun checkOpenTo(course: Course, majors: List<String>,
                            courseConstraints: MutableMap<String, ParsedPrereqData>): ValidationResult {
        // Only consider for math programs, and since there is no mapping between programs and faculties,
        // so have to include explicit elements for math faculty
        val facultyIncludedMajors: Set<String> = (majors + "Faculty of Mathematics").toSet()

        // Currently only checks for majors. Not sure if minors or specializations would satisfy this requirement
        val notOpenTo: Set<String> = courseConstraints[course.courseID]!!.notOpenTo.toSet()
        val onlyOpenTo: Set<String> = courseConstraints[course.courseID]!!.onlyOpenTo.toSet()
        var commonMajors: Set<String> = notOpenTo.intersect(facultyIncludedMajors)
        if (commonMajors.isNotEmpty()) {
            return ValidationResult.NotOpenTo
        }
        commonMajors = onlyOpenTo.intersect(facultyIncludedMajors)
        if (onlyOpenTo.isNotEmpty() and commonMajors.isEmpty()) {
            return ValidationResult.NotOpenTo
        }
        return ValidationResult.Success
    }

    private fun getTotalRequiredCourses(requirements: Requirements): Int {
        val mandatoryCoursesCount: Int = requirements.mandatoryCourses.size
        val optionalCoursesCount: Int = requirements.optionalCourses.sumOf { it.nOf }
        return mandatoryCoursesCount + optionalCoursesCount
    }

    private fun checkDegreeCourseRequirements(schedule: Schedule,
                                              requirements: Requirements): MutableSet<OverallValidationResult> {
        val validationResult = mutableSetOf<OverallValidationResult>()
        val allSchedulesCourses: List<String> = schedule.values.flatten().map { it.courseID }
        val totalRequiredCourseCount: Int = getTotalRequiredCourses(requirements)

        // If there is not enough courses, then result is guaranteed to not be success,
        // Then there is no point checking afterwards
        if (allSchedulesCourses.size < totalRequiredCourseCount) return mutableSetOf(OverallValidationResult.NotEnoughCourse)

        // First check if all mandatory courses are present in the schedule
        val mandatoryRes: Boolean = requirements.mandatoryCourses.all { course ->
            val courseStr = "${course.subject} ${course.code}"
            courseStr in allSchedulesCourses
        }
        if (!mandatoryRes) validationResult.add(OverallValidationResult.NotMeetMandatoryCourses)

        // Check optional courses
        val optionalRes: Boolean = requirements.optionalCourses.all { optionalCourses ->
            var coursesNeeded = optionalCourses.nOf
            optionalCourses.courses.forEach { course ->
                val courseString = "${course.subject} ${course.code}"
                if (courseString in allSchedulesCourses) {
                    coursesNeeded--
                }
                if (coursesNeeded == 0) return@all true
            }
            false
        }
        if (!optionalRes) validationResult.add(OverallValidationResult.NotMeetOptionalCourses)
        if (mandatoryRes and optionalRes) validationResult.add(OverallValidationResult.Success)
        return validationResult
    }
    
    fun validateSchedule(schedule: Schedule, majors: List<String>, sequenceMap: Map<String, String>,
                         requirements: Requirements): ScheduleValidationOutput {
        // Since major names stored in the DB does not contain "Bachelor of ", so need to adjust names
        val adjustedMajorNames: List<String> = majors.map { it.replace("Bachelor of ", "") }.toList()
        val courseValidity = mutableMapOf<String, MutableList<List<ValidationResult>>>()
        val degreeValidity = mutableSetOf<OverallValidationResult>()
        var overallResult = true
        val courseList: List<String> = schedule.values.flatten().map { it.courseID }
        val takenSoFar: MutableList<String> = mutableListOf()
        val takenAndTakingSoFar: MutableList<String> = mutableListOf()
        val courseConstraints = prerequisiteRepo.getParsedPrereqData(courseList)

        // First check communication courses
         val commRes: OverallValidationResult = checkList1CommunicationCourse(schedule, adjustedMajorNames)
         if (commRes != OverallValidationResult.Success) {
             degreeValidity.add(commRes)
             // Currently, not satisfying communication course timeline is only a warning.
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
                    checkOpenTo(course, adjustedMajorNames, courseConstraints)
                )
                // Only keep the invalid reasons for that course
                courseResult.removeIf { it == ValidationResult.Success }
                if (courseResult.isNotEmpty()) overallResult = false
                termResult.add(courseResult.toList())
                takenSoFar.add(course.courseID)
            }
            courseValidity[term] = termResult
        }

        // Check if degree course requirements are satisfied
        val degreeRes: MutableSet<OverallValidationResult> = checkDegreeCourseRequirements(schedule, requirements)
        degreeRes.removeIf { it == OverallValidationResult.Success }
        if (degreeRes.isNotEmpty()) {
            degreeValidity.addAll(degreeRes)
            // If not enough courses is selected in the schedule, then don't consider it as being invalid
            // Only show as a warning since user may fill out full schedule later
            if (OverallValidationResult.NotEnoughCourse !in degreeRes) overallResult = false
        }

        return ScheduleValidationOutput(courseValidity, degreeValidity.toList(), overallResult)
    }
}
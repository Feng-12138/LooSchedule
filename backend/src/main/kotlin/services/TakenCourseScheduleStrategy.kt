package services

import jakarta.inject.Inject
import services.utilities.*

class TakenCourseScheduleStrategy: ScheduleStrategy {
    @Inject
    private lateinit var requirementsParser: RequirementsParser

    @Inject
    private lateinit var coursePlanner: CoursePlanner

    @Inject
    private lateinit var termMapperService: TermMapperService

    @Inject
    private lateinit var sequenceGenerator: SequenceGenerator

    @Inject
    private lateinit var service: IService

    override fun generateSchedule(plan: AcademicPlan, recommendedCourses: MutableList<Course>): TermSchedule {
        val requirements = service.getRequirements(plan)
        val takenCourses = plan.coursesTaken
        var data = CommonRequirementsData(requirements, 0)

        if (takenCourses.isNotEmpty()) {
            // remove 136L if 136 is taken means it's not a co-req
            if (takenCourses.contains("CS 136")) {
                requirements.mandatoryCourses.remove(Course("CS", "136L"))
            }
            requirements.mandatoryCourses.removeIf { mandatoryCourse ->
                val course = mandatoryCourse.subject + " " + mandatoryCourse.code
                takenCourses.contains(course)
            }
            data = requirementsParser.combineOptionalRequirements(requirements, takenCourses)
        }

        val sequenceMap = sequenceGenerator.generateSequence(plan.sequence, plan.currentTerm)
        val selectedCourses = coursePlanner.getCoursesPlanToTake(
            plan.startYear,
            data.requirements,
            plan.majors,
            sequenceMap = sequenceMap,
            takenCourses,
        )

        val schedule = termMapperService.mapCoursesToSequence(
            courseData = CourseDataClass(fallCourses = selectedCourses["F"]!!, springCourses = selectedCourses["S"]!!, winterCourses = selectedCourses["W"]!!,
                prereqMap = coursePlanner.getPrereqMap()),
            sequenceMap = sequenceMap,
            previousTakenCourses = takenCourses,
        )

        return TermSchedule(schedule = schedule)
    }
}
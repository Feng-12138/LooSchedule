package services

import jakarta.inject.Inject
import services.utilities.*

class DefaultScheduleStrategy: ScheduleStrategy {
    @Inject
    private lateinit var service: IService

    @Inject
    private lateinit var coursePlanner: CoursePlanner

    @Inject
    private lateinit var termMapperService: TermMapperService

    @Inject
    private lateinit var sequenceGenerator: SequenceGenerator


    override fun generateSchedule(plan: AcademicPlan, recommendedCourses: MutableList<Course>): TermSchedule {
        val requirements = service.getRequirements(plan)

        val sequenceMap = sequenceGenerator.generateSequence(plan.sequence, plan.currentTerm)
        val selectedCourses = coursePlanner.getCoursesPlanToTake(
            plan.startYear,
            requirements,
            plan.majors,
            sequenceMap = sequenceMap,
        )

//        println(selectedCourses["F"]!!.map { it.courseID })
//        println(selectedCourses["W"]!!.map { it.courseID })
//        println(selectedCourses["S"]!!.map { it.courseID })

        val schedule = termMapperService.mapCoursesToSequence(
            courseData = CourseDataClass(fallCourses = selectedCourses["F"]!!, springCourses = selectedCourses["S"]!!, winterCourses = selectedCourses["W"]!!,
                prereqMap = coursePlanner.getPrereqMap()),
            sequenceMap = sequenceMap,
            previousTakenCourses = listOf()
        )

        return TermSchedule(schedule = schedule)
    }
}
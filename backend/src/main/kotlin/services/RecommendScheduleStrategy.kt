package services

import jakarta.inject.Inject
import repositories.CourseRepo

class RecommendScheduleStrategy: ScheduleStrategy {
    @Inject
    private lateinit var service: IService

    @Inject
    private lateinit var coursePlanner: CoursePlanner

    @Inject
    private lateinit var termMapperService: TermMapperService

    @Inject
    private lateinit var sequenceGenerator: SequenceGenerator

    @Inject
    private lateinit var courseRepo: CourseRepo

    @Inject
    private lateinit var requirementsParser: RequirementsParser

    override fun generateSchedule(
        plan: AcademicPlan,
        recommendedCourses: MutableList<services.Course>,
    ): TermSchedule {
        val requirements = service.getRequirements(plan)
        var data = CommonRequirementsData(requirements, 0)

        var recommendSuccess = 0
        if (recommendedCourses.isNotEmpty()) {
            val initialSize = recommendedCourses.size
            recommendedCourses.removeIf { recCourse ->
                requirements.mandatoryCourses.contains(recCourse)
            }
            recommendSuccess += initialSize - recommendedCourses.size

            data = requirementsParser.combineOptionalRequirements(
                requirements = data.requirements,
                checkCourses = recommendedCourses.map { it.subject + " " + it.code })
            recommendSuccess += data.commonSize
        }

        val sequenceMap = sequenceGenerator.generateSequence(plan.sequence, plan.currentTerm)
        val selectedCourses = coursePlanner.getCoursesPlanToTake(
            plan.startYear,
            data.requirements,
            plan.majors,
            sequenceMap = sequenceMap,
            recommendedCourses = courseRepo.getBySubjectCode(recommendedCourses.toSet()),
        )

        println(selectedCourses["F"]!!.map { it.courseID })
        println(selectedCourses["W"]!!.map { it.courseID })
        println(selectedCourses["S"]!!.map { it.courseID })

        val schedule = termMapperService.mapCoursesToSequence(
            courseData = CourseDataClass(
                fallCourses = selectedCourses["F"]!!,
                springCourses = selectedCourses["S"]!!,
                winterCourses = selectedCourses["W"]!!,
                prereqMap = coursePlanner.getPrereqMap()
            ),
            sequenceMap = sequenceMap,
            previousTakenCourses = listOf(),
        )

        val countRecommendations = recommendedCourses.count { course ->
            schedule.any { (_, courseListInMap) ->
                courseListInMap.map { Course(it.subject, it.code) }.contains(course)
            }
        }

        val message = if (countRecommendations >= 5) {
            "success"
        } else {
            "We could only fit $countRecommendations recommended courses in your schedule, " +
                    "consider review it or change a program"
        }

        return TermSchedule(
            schedule = schedule,
            successRecCount = countRecommendations,
            recommendedCourses = recommendedCourses.map { it.subject + " " + it.code },
            message = message,
        )
    }
}
package services

import jakarta.inject.Inject

class Scheduler {

    @Inject
    private lateinit var defaultScheduleStrategy: ScheduleStrategy

    @Inject
    private lateinit var takenCourseScheduleStrategy: ScheduleStrategy

    @Inject
    private lateinit var recommendScheduleStrategy: ScheduleStrategy

    fun schedule(plan: AcademicPlan,  recommendedCourses: MutableList<Course> = mutableListOf()): TermSchedule {
        return if (plan.coursesTaken.isEmpty()) {
            defaultScheduleStrategy.generateSchedule(plan, recommendedCourses)
        } else if (recommendedCourses.isEmpty()) {
            takenCourseScheduleStrategy.generateSchedule(plan, recommendedCourses)
        } else {
            recommendScheduleStrategy.generateSchedule(plan, recommendedCourses)
        }
    }
}
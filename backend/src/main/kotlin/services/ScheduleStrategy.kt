package services

import services.utilities.Course

interface ScheduleStrategy {
    fun generateSchedule(
        plan: AcademicPlan,
        recommendedCourses: MutableList<Course>,
    ): TermSchedule
}
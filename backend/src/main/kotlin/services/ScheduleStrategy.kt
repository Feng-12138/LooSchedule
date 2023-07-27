package services

interface ScheduleStrategy {
    fun generateSchedule(
        plan: AcademicPlan,
        recommendedCourses: MutableList<services.Course>,
    ): TermSchedule
}
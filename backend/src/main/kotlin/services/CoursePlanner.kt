package services

import entities.Course
import entities.Year
import jakarta.inject.Inject
import repositories.CommunicationRepo
import repositories.CourseRepo

class CoursePlanner {
    @Inject
    private lateinit var courseRepo: CourseRepo
    @Inject
    private lateinit var communicationRepo: CommunicationRepo

    private val mathSubjects = listOf("MATH", "STAT", "CS", "CO", "ACTSC")

    private val courseComparator = Comparator<Course> { course1, course2 ->
        val averageRating1 = (course1.likedRating!!.plus(course1.easyRating!!.plus(course1.usefulRating!!))) / 3.0
        val averageRating2 = (course2.likedRating!!.plus(course2.easyRating!!.plus(course2.usefulRating!!))) / 3.0
        averageRating2.compareTo(averageRating1)
    }

    private fun selectCoursesFromOptional(optionalCourseList: OptionalCourses, mathCourses: MutableSet<Course>, nonMathCourses: MutableSet<Course>): Unit {
        val selectedCourses: Set<Course> = optionalCourseList.courses.toList().sortedWith(courseComparator).take(optionalCourseList.nOf).toSet()
        selectedCourses.forEach {
            if (mathSubjects.contains(it.subject)) {
                mathCourses.add(it)
            } else {
                nonMathCourses.add(it)
            }
        }
    }

    private fun selectCommunication(startYear: Year, nonMathCourses: MutableSet<Course>): Unit {
        val sortedList1Courses: MutableList<Course> = communicationRepo.getList1ByYear(startYear.toInt()).toList().sortedWith(courseComparator).toMutableList()
        nonMathCourses.add(sortedList1Courses[0])
        sortedList1Courses.removeAt(0)
        val sortedComCourses: List<Course> = (sortedList1Courses + communicationRepo.getList2ByYear(startYear.toInt()).toList()).sortedWith(courseComparator)
        nonMathCourses.add(sortedComCourses[0])
    }

    private data class OptionalCourses (
        var nOf: Int = 1,
        val courses: MutableSet<Course> = mutableSetOf(),
    )

    fun getCoursesPlanToTake(startYear: Year, requirements: Requirements): Pair<MutableSet<Course>, MutableSet<Course>> {
        val mathCourses: MutableSet<Course> = emptySet<Course>() as MutableSet<Course>
        val nonMathCourses: MutableSet<Course> = emptySet<Course>() as MutableSet<Course>
        val mandatoryCourses: MutableSet<Course> = courseRepo.getBySubjectCode(requirements.mandatoryCourses)
        val optionalCourses: MutableSet<OptionalCourses> = requirements.optionalCourses.map {
            OptionalCourses(it.nOf, courseRepo.getBySubjectCode(it.courses))
        }.toMutableSet()

        for (mandatoryCourse in mandatoryCourses) {
            if (mathSubjects.contains(mandatoryCourse.subject)) {
                mathCourses.add(mandatoryCourse)
                continue
            }
            nonMathCourses.add(mandatoryCourse)
        }

        for (optionalCourseList in optionalCourses) {
            selectCoursesFromOptional(optionalCourseList, mathCourses, nonMathCourses)
        }

        selectCommunication(startYear, nonMathCourses)

        // Todo: Add breadth

        return Pair(mathCourses, nonMathCourses)
    }
}
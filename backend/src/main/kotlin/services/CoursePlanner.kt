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
        val course1Liked = course1.likedRating ?: 0.0
        val course2Liked = course2.likedRating ?: 0.0
        val course1Easy = course1.easyRating ?: 0.0
        val course2Easy = course2.easyRating ?: 0.0
        val course1Useful = course1.usefulRating ?: 0.0
        val course2Useful = course2.usefulRating ?: 0.0
        val averageRating1 = (course1Liked.plus(course1Easy).plus(course1Useful)) / 3.0
        val averageRating2 = (course2Liked.plus(course2Easy).plus(course2Useful)) / 3.0
        averageRating2.compareTo(averageRating1)
    }

    private fun selectCoursesFromOptional(
        optionalCourseList: OptionalCourses,
        mathCourses: MutableSet<Course>,
        nonMathCourses: MutableSet<Course>
    ) {
        val selectedCourses = optionalCourseList.courses.sortedWith(courseComparator).take(optionalCourseList.nOf).toSet()
//        println(selectedCourses.map { it.courseID })
        selectedCourses.forEach {
            if (mathSubjects.contains(it.subject)) {
                mathCourses.add(it)
            } else {
                nonMathCourses.add(it)
            }
        }
    }

    private fun selectCommunication(startYear: Year, nonMathCourses: MutableSet<Course>) {
        val sortedList1Courses = communicationRepo.getListNByYear(startYear.toInt(), 1).toList().sortedWith(courseComparator).toMutableList()
        nonMathCourses.add(sortedList1Courses[0])
        sortedList1Courses.removeAt(0)
        val sortedComCourses: List<Course> = (sortedList1Courses + communicationRepo.getListNByYear(startYear.toInt(), 2).toList()).sortedWith(courseComparator)
        nonMathCourses.add(sortedComCourses[0])
    }

    private data class OptionalCourses (
        var nOf: Int = 1,
        val courses: MutableSet<Course> = mutableSetOf()
    )

    fun getCoursesPlanToTake(startYear: Year, requirements: Requirements): Pair<MutableSet<Course>, MutableSet<Course>> {
        val mathCourses: MutableSet<Course> = mutableSetOf()
        val nonMathCourses: MutableSet<Course> = mutableSetOf()
        val mandatoryCourses: MutableSet<Course> = courseRepo.getBySubjectCode(requirements.mandatoryCourses)
        val optionalCourses: MutableSet<OptionalCourses> = requirements.optionalCourses.map {
            OptionalCourses(it.nOf, courseRepo.getBySubjectCode(it.courses))
        }.toMutableSet()

        for (mandatoryCourse in mandatoryCourses) {
            if (mathSubjects.contains(mandatoryCourse.subject)) {
                mathCourses.add(mandatoryCourse)
            } else {
                nonMathCourses.add(mandatoryCourse)
            }
        }

        for (optionalCourseList in optionalCourses) {
            selectCoursesFromOptional(optionalCourseList, mathCourses, nonMathCourses)
        }

        selectCommunication(startYear, nonMathCourses)

        return Pair(mathCourses, nonMathCourses)
    }
}
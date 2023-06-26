package services
import entities.Course

import entities.MinimumLevel

data class course(
    var availability: String = "",
    var courses : MutableList<MutableList<String>> = mutableListOf(),
)

data class courseDataClass(
    var numOfCoursesPerTerm: Int = 5,
    var takeCourseInWT: Boolean = false,
    var mathCourses: MutableSet<Course> = mutableSetOf(),
    var nonMathCourses: MutableSet<Course> = mutableSetOf(),
)

data class prereqDataClass(
    var courseName: String = "",
    var coursesString: String = "",
    var minimumLevel: String = ""
)

data class parsedPrereqDataClass(
    var coursesString: String = "",
    var courses : MutableList<MutableList<String>> = mutableListOf(),
    var minimumLevel: String = ""
)

class termMapperService(prereqData: MutableList<prereqDataClass>) {
    private var takenCourses : MutableList<String> = mutableListOf()
    private val prereqDataPrivate = prereqData

//    private fun filterNeededCourses(courseData: courseDataClass) : MutableMap<String, prereqDataClass> {
//        var retval = mutableMapOf<String, prereqDataClass>()
//        for (singlePrereqData in prereqDataPrivate) {
//            for (course in courseData.mathCourses) {
//                if (course == singlePrereqData.courseName) {
//                    retval[course] = singlePrereqData
//                }
//            }
//        }
//        return retval
//    }

    fun generateCourseForTerm(termName : String, season: String, number: Int, mathCourse: MutableList<Course>, nonMathCourse: MutableList<Course>) {
        val notTakenNonMathCourse = mutableListOf<Course>()
        val notTakenMathCourse = mutableListOf<Course>()
        val satisfyConstraintMathCourse = mutableListOf<Course>()
        val satisfyConstraintNonMathCourse = mutableListOf<Course>()
        for (Course in mathCourse) {
            if (Course.courseID !in takenCourses) {
                notTakenMathCourse.add(Course)
            }
        }
        for (Course in nonMathCourse) {
            if (Course.courseID !in takenCourses) {
                notTakenNonMathCourse.add(Course)
            }
        }

        for (Course in notTakenMathCourse)
        if (termName.contains("WT")) {

        }

    }

    fun mapCoursesToSequence(courseData: courseDataClass, sequenceMap: Map<String, String>) : MutableMap<String, MutableList<Course>> {
        val neededPrereqs : MutableMap<String, parsedPrereqDataClass> = mutableMapOf()
        var countCourseTerm = mutableMapOf<String, Int>()
        if (courseData.takeCourseInWT) {
            if ("WT1" !in sequenceMap) {
                println("impossible to achieve")
                return mutableMapOf()
            }
        }
        for ((key, value) in sequenceMap) {
            if (key.contains("WT")) {
                if (courseData.takeCourseInWT) {
                    countCourseTerm[key] = 1
                } else {
                    countCourseTerm[key] = 0
                }
            } else {
                countCourseTerm[key] = courseData.numOfCoursesPerTerm
            }
        }


    }
}
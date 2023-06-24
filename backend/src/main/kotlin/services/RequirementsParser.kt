package services

class RequirementsParser {
    // requirementsData:
//    ["1:CS 115,CS 135,CS 145;1:CS 136,CS 146;1:MATH 127,MATH 137,MATH 147;1:MATH 128,MATH 138,MATH 148;1:MATH 135,MATH 145;1:MATH 136,MATH 146;1:MATH 239,MATH 249;1:STAT 230,STAT 240;1:STAT 231,STAT 241;1:CS 136L;1:CS 240,CS 240E;1:CS 241,CS 241E;1:CS 245,CS 245E;1:CS 246,CS 246E;1:CS 251,CS 251E;1:CS 341;1:CS 350;3:CS 340-CS 398,CS 440-CS 489;2:CS 440-CS 489;1:CO 487,CS 440-CS 498,CS 499T,STAT 440,CS 6xx,CS 7xx",
//    "1:CS 115,CS 135,CS 145;1:CS 136,CS 146;1:MATH 127,MATH 137,MATH 147;1:MATH 128,MATH 138,MATH 148;1:MATH 135,MATH 145;1:MATH 136,MATH 146;1:MATH 239,MATH 249;1:STAT 230,STAT 240;1:STAT 231,STAT 241;1:CS 136L;1:CS 240,CS 240E;1:CS 241,CS 241E;1:CS 245,CS 245E;1:CS 246,CS 246E;1:CS 251,CS 251E;1:CS 341;1:CS 350;3:CS 340-CS 398,CS 440-CS 489;2:CS 440-CS 489;1:CO 487,CS 440-CS 498,CS 499T,STAT 440,CS 6xx,CS 7xx"]
    fun parseRequirements(requirementsData: List<String>) : Requirements {
        val optionalCourses = mutableSetOf<OptionalCourses>()
        val mandatoryCourses = mutableSetOf<Course>()
        for (requirements in requirementsData) {
            val requirement = requirements.split(";")
            for (r in requirement) {
                val temp = r.split(":")
                val nOf = temp[0]
                val courseList = temp[1].split(",")
                val courses = mutableSetOf<Course>()
                for (course in courseList) {
                    val newCourse = course.split(" ")
                    courses.add(
                        Course(
                            subject = newCourse[0],
                            code = newCourse[1],
                        )
                    )
                }
                if (nOf.toInt() == courses.size) {
                    mandatoryCourses.addAll(courses)
                } else {
                    optionalCourses.add(
                        OptionalCourses(
                            nOf = nOf,
                            courses = courses,
                        )
                    )
                }
            }
        }

        return Requirements(
            optionalCoursesList = optionalCourses,
            mandatoryCourses = mandatoryCourses,
        )
    }
}


data class Course (
    val subject: String,
    val code: String,
)

data class OptionalCourses (
    val nOf: String? = "",
    val courses: Set<Course>? = null
)

data class Requirements (
    val optionalCoursesList: Set<OptionalCourses>? = null,
    val mandatoryCourses: Set<Course>? = null
)

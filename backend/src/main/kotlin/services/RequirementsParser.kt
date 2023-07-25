package services

import java.util.concurrent.ConcurrentHashMap

class RequirementsParser {
    // requirementsData example:
    //    ["1:CS 115,CS 135,CS 145;1:CS 136,CS 146;1:MATH 127,MATH 137,MATH 147;1:MATH 128,MATH 138,MATH 148;1:MATH 135,MATH 145;1:MATH 136,MATH 146;1:MATH 239,MATH 249;1:STAT 230,STAT 240;1:STAT 231,STAT 241;1:CS 136L;1:CS 240,CS 240E;1:CS 241,CS 241E;1:CS 245,CS 245E;1:CS 246,CS 246E;1:CS 251,CS 251E;1:CS 341;1:CS 350;3:CS 340-CS 398,CS 440-CS 489;2:CS 440-CS 489;1:CO 487,CS 440-CS 498,CS 499T,STAT 440,CS 6xx,CS 7xx"]
    fun parseRequirements(requirementsData: List<String>): Requirements {
        return finalizeRequirements(initialReqParse(requirementsData))
    }

    private fun parseBoundedRangeCourses(course: String, courses: MutableSet<Course>): Unit {
        val courseRange = course.split("-")
        val courseLowerBound = courseRange[0].split(" ")
        val courseUpperBound = courseRange[1].split(" ")
        val courseSubject = courseLowerBound[0]
        val courseLowerCode = courseLowerBound[1].toInt()
        val courseUpperCode = courseUpperBound[1].toInt()
        // Add all possible course codes now. Invalid ones will be filter out when querying DB.
        for (i in courseLowerCode..courseUpperCode) {
            courses.add(
                Course(
                    subject = courseSubject,
                    code = i.toString(),
                )
            )
        }
    }

    // Parse a single requirement clause. Eg. requirement = "1:CS 115,CS 135,CS 145"
    private fun parseSingleReqClause(requirement: String, optionalCourses: MutableSet<OptionalCourses>,
                                     mandatoryCourses: MutableSet<Course>): Unit {
        val courses = mutableSetOf<Course>()
        val temp = requirement.split(":")
        val nOf = temp[0]
        val courseList = temp[1].split(",")
        for (course in courseList) {
            // Eg. CS 440-CS 489
            if (course.contains("-")) {
                parseBoundedRangeCourses(course, courses)
            } else {
                val newCourse = course.split(" ")
                // Eg. CS 7xx
                if (newCourse[1].contains("xx")) {
                    // Add all possible values of xx as course. These courses will be filtered when querying DB.
                    for (i in 0..9) {
                        courses.add(
                            Course(
                                subject = newCourse[0],
                                code = newCourse[1][0] + "0" + i.toString(),
                            )
                        )
                    }
                    for (i in 10..99) {
                        courses.add(
                            Course(
                                subject = newCourse[0],
                                code = newCourse[1][0] + i.toString(),
                            )
                        )
                    }
                } else {
                    courses.add(
                        Course(
                            subject = newCourse[0],
                            code = newCourse[1],
                        )
                    )
                }
            }
        }
        if (nOf.toInt() == courses.size) {
            mandatoryCourses.addAll(courses)
        } else {
            optionalCourses.add(
                OptionalCourses(
                    nOf = nOf.toInt(),
                    courses = courses,
                )
            )
        }
    }

    // Extract all courses needed based on requirement.
    fun initialReqParse(requirementsData: List<String>): Requirements {
        val optionalCourses = mutableSetOf<OptionalCourses>()
        val mandatoryCourses = mutableSetOf<Course>()
        for (requirements in requirementsData) {
            val requirement = requirements.split(";")
            for (r in requirement) {
                parseSingleReqClause(r, optionalCourses, mandatoryCourses)
            }
        }

        return Requirements(
            optionalCourses = optionalCourses,
            mandatoryCourses = mandatoryCourses,
        )
    }

    // Finalize requirements by restraining some optional choices of courses as mandatory
    private fun finalizeRequirements(requirements: Requirements): Requirements {
        val optionalCoursesList = requirements.optionalCourses.toMutableList()
        val mandatoryCourses = requirements.mandatoryCourses.toMutableList()
        val commonTable: ConcurrentHashMap<Course, Int> = ConcurrentHashMap()

        for (i in 0 until optionalCoursesList.size) {
            for (j in i + 1 until optionalCoursesList.size) {
                val coursesI = optionalCoursesList[i]
                val coursesJ = optionalCoursesList[j]
                val commonCourses = coursesJ.courses.filter { coursesI.courses.contains(it) }.toSet()
                for (commonCourse in commonCourses) {
                    commonTable.compute(commonCourse) { _, count -> count?.plus(1) ?: 1 }
                }
            }
        }

        val remainingOptionalCourses = mutableListOf<OptionalCourses>()
        for (optionalCourses in optionalCoursesList) {
            var commonCourses = optionalCourses.courses.filter { it in commonTable.keys }.toSet()
            if (commonCourses.size < optionalCourses.nOf) {
                optionalCourses.nOf -= commonCourses.size
                optionalCourses.courses.removeAll(commonCourses)
                remainingOptionalCourses.add(optionalCourses)
            } else {
                commonCourses = commonTable.filterKeys { it in commonCourses }
                    .toList()
                    .sortedBy { (_, value) -> value }
                    .map { it.first }
                    .take(optionalCourses.nOf)
                    .toSet()
                mandatoryCourses.addAll(commonCourses)
            }
        }

        remainingOptionalCourses.removeIf { optionalCourses ->
            mandatoryCourses.containsAll(optionalCourses.courses)
        }

        // add breadth and depth
        mandatoryCourses.addAll(
            listOf(
                Course("ECON", "101"),
                Course("ECON", "102"),
                Course("ECON", "371"),
            )
        )

        mandatoryCourses.addAll(
            listOf(
                Course("MUSIC", "116"),
                Course("PHYS", "111"),
                Course("CHEM", "102"),
            )
        )

        return Requirements(
            optionalCourses = remainingOptionalCourses.toMutableSet(),
            mandatoryCourses = mandatoryCourses.toMutableSet(),
        )
    }
}


data class Course (
    val subject: String,
    val code: String,
)

data class OptionalCourses (
    var nOf: Int = 1,
    val courses: MutableSet<Course> = mutableSetOf(),
)

data class Requirements (
    val optionalCourses: MutableSet<OptionalCourses> = mutableSetOf(),
    val mandatoryCourses: MutableSet<Course> = mutableSetOf(),
)
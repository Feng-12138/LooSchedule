package services
import entities.Course
import repositories.ParsedPrereqData

class TermMapperService {
    private var takeCourseInWT = false
    private var takenCourses: MutableList<String> = mutableListOf()

    private val listOneCourses =
        listOf("COMMST 100", "COMMST 223", "EMLS 101R", "EMLS 102R", "EMLS 129R", "ENGL 129R", "ENGL 109")

    fun mapCoursesToSequence(
        courseData: CourseDataClass,
        sequenceMap: Map<String, String>,
        previousTakenCourses: List<String>,
    ): MutableMap<String, MutableList<Course>> {
        takenCourses.addAll(previousTakenCourses)
        val list = courseData.fallCourses.map { it.courseID }.toMutableList()
        list.addAll(courseData.winterCourses.map { it.courseID })
        if (courseData.springCourses.size > 0) {
            list.addAll(courseData.springCourses.map { it.courseID })
        }
        val prereqsData = courseData.prereqMap.filterKeys { it in list }
        val countCourseTerm = mutableMapOf<String, Int>()
        val generatedSchedule = mutableMapOf<String, MutableList<Course>>()
        val totalNumberCourses =
            courseData.fallCourses.size + courseData.winterCourses.size + courseData.springCourses.size
        var coursePerTerm: Int
        var remainder: Int
        if (takeCourseInWT) {
            coursePerTerm = (totalNumberCourses - 6) / 8
            remainder = (totalNumberCourses - 6) % 8
        } else {
            coursePerTerm = (totalNumberCourses) / 8
            remainder = (totalNumberCourses) % 8
        }
        if (coursePerTerm == 5 && remainder != 0) {
            takeCourseInWT = true
        }
        if (coursePerTerm > 5) {
            takeCourseInWT = true
        }
        coursePerTerm = 5
        remainder = 0
        for ((key, _) in sequenceMap) {
            if (key.contains("WT")) {
                if (takeCourseInWT) {
                    countCourseTerm[key] = 1
                } else {
                    countCourseTerm[key] = 0
                }
            } else {
                if (remainder > 0) {
                    countCourseTerm[key] = coursePerTerm + 1
                    remainder--
                } else {
                    countCourseTerm[key] = coursePerTerm
                }
            }
        }
        for ((key, value) in sequenceMap) {
            val courseList = generateCourseForTermSeason(
                season = value,
                parsedPrereqDataMap = prereqsData.toMutableMap(),
                fallCourses = courseData.fallCourses,
                springCourses = courseData.springCourses,
                winterCourses = courseData.winterCourses,
                numCourse = countCourseTerm[key]!!.toInt(),
                termName = key
            )
            val coursesTakeThisTerm = courseList.map { it.courseID }
            if (coursesTakeThisTerm != null) {
                takenCourses.addAll(coursesTakeThisTerm)
            }
            println(coursesTakeThisTerm)
            generatedSchedule[key] = courseList
//            println(coursesTakeThisTerm)
        }
        takenCourses.clear()
        courseData.fallCourses.clear()
        courseData.springCourses.clear()
        courseData.winterCourses.clear()
        return generatedSchedule
    }

    private fun checkConstraint(
        course: Course,
        parsedDataMap: MutableMap<String, ParsedPrereqData>,
        retvalList: List<String>,
        season: String,
        level: String
    ): Boolean {
        val selectedCourseData = parsedDataMap[course.courseID]
        if (selectedCourseData!!.minimumLevel != "" && selectedCourseData.minimumLevel > level) {
            return false
        }
        if (course.courseID in takenCourses || course.courseID in retvalList) {
            return false
        }
        if (!course.availability!!.contains(season)) {
            return false
        }

        var satisfyPrereq = false
        var satisfyCoreq = false
        for (prereqCourseOption in selectedCourseData.courses) {
            var satisfy = true
            if (prereqCourseOption.size == 0) {
                continue
            }
            for (preReqCourse in prereqCourseOption) {
                if (preReqCourse !in takenCourses) {
                    satisfy = false
                    break
                }
            }
            if (satisfy) {
                satisfyPrereq = satisfy
            }
        }
        if (selectedCourseData.courses.size == 0) {
            satisfyPrereq = true
        }

        for (coreqCourseOption in selectedCourseData.coreqCourses) {
            var satisfy = true
            if (coreqCourseOption.size == 0) {
                continue
            }
            for (coReqCourse in coreqCourseOption) {
                if (coReqCourse !in takenCourses && coReqCourse !in retvalList) {
                    satisfy = false
                    break
                }
            }
            if (satisfy) {
                satisfyCoreq = satisfy
            }
        }
        if (selectedCourseData.coreqCourses.size == 0 || course.courseID == "CS 136" || course.courseID == "CS 146" || course.courseID == "CS 136L") {
            satisfyCoreq = true
        }
        return !(!satisfyPrereq || !satisfyCoreq)
    }

    private fun generateCourseForTermSeason(
        termName: String,
        season: String,
        numCourse: Int,
        fallCourses: MutableSet<Course>,
        winterCourses: MutableSet<Course>,
        springCourses: MutableSet<Course>,
        parsedPrereqDataMap: MutableMap<String, ParsedPrereqData>
    ): MutableList<Course> {
        val arrangeCourses = mutableSetOf<Course>()
        arrangeCourses.addAll(fallCourses)
        arrangeCourses.addAll(winterCourses)
        arrangeCourses.addAll(springCourses)
        var countAdded = 0
        val notTakenCourses = mutableListOf<Course>()
        val retvalList = mutableListOf<Course>()
        for (course in arrangeCourses) {
            if (course.courseID !in takenCourses) {
                notTakenCourses.add(course)
            }
        }
        notTakenCourses.sortByDescending { it.priorityPoint }
        if (termName == "1A") {
            for (course in notTakenCourses) {
                if (countAdded >= numCourse) {
                    break
                }
                if (course.courseID == "MATH 135" || course.courseID == "CS 135" || course.courseID == "MATH 145"
                    || course.courseID == "MATH 137" || course.courseID == "CS 115"
                    || course.courseID == "MATH 147" || course.courseID == "CS 145" || course.courseID in listOneCourses
                ) {
                    retvalList.add(course)
                    countAdded++
                    continue
                }
            }
        }
        if (termName == "1B") {
            for (course in notTakenCourses) {
                if (course.courseID == "MATH 136" || course.courseID == "MATH 146" || course.courseID == "MATH 138" ||
                    course.courseID == "MATH 148" || course.courseID == "CS 136" || course.courseID == "CS 116"
                    || course.courseID == "CS 146" || course.courseID == "STAT 230"
                ) {
                    retvalList.add(course)
                    countAdded++
                    continue
                }
            }
        }
        for (course in notTakenCourses) {
            if (countAdded >= numCourse) {
                break
            }
            if (course.courseID in takenCourses) {
                continue
            }

            val result = checkConstraint(
                course,
                parsedDataMap = parsedPrereqDataMap,
                retvalList = retvalList.map { it.courseID }, season = season, level = termName)
            if (result) {
                retvalList.add(course)
                countAdded++
            }
        }
        if (retvalList.size > 0) {
            takenCourses.addAll(retvalList.map { it.courseID })
        }
        return retvalList
    }
}

data class CourseDataClass(
    var numOfCoursesPerTerm: Int = 5,
    var takeCourseInWT: Boolean = false,
    var mathCourses: MutableSet<Course> = mutableSetOf(),
    var nonMathCourses: MutableSet<Course> = mutableSetOf(),
    var fallCourses: MutableSet<Course> = mutableSetOf(),
    var springCourses: MutableSet<Course> = mutableSetOf(),
    var winterCourses: MutableSet<Course> = mutableSetOf(),
    var prereqMap: MutableMap<String, ParsedPrereqData>
)

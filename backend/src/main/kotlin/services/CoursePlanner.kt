package services

import entities.Course
import entities.Year
import jakarta.inject.Inject
import repositories.CommunicationRepo
import repositories.CourseRepo
import repositories.ParsedPrereqData
import repositories.PrerequisiteRepo

val TOTAL_COURSES = 45
val TOTAL_MATH = 30
val TOTAL_NON_MATH = 15
class CoursePlanner {
    @Inject
    private lateinit var courseRepo: CourseRepo

    @Inject
    private lateinit var communicationRepo: CommunicationRepo

    @Inject
    private lateinit var prerequisiteRepo: PrerequisiteRepo

    val mathSubjects = listOf("MATH", "STAT", "CS", "CO", "ACTSC", "AMATH", "PMATH")

    val ListOneCourses = listOf("COMMST 100", "COMMST 223", "EMLS 101R", "EMLS 102R", "EMLS 129R", "ENGL 129R", "ENGL 109")

    val majorMap = mapOf<String, String>("Statistics" to "STAT", "Computer Science" to "CS",
        "Applied Mathematics" to "AMATH", "Combinatorics and Optimization" to "CO", "Pure Mathematics" to "PMath")


    val listofTakenCourses = mutableListOf<String>()

    private var countTakenNonMathCourse = 0
    private var countTakenMathCourse = 0


    val courseComparator = Comparator<Course> { course1, course2 ->
        val course1Liked = course1.likedRating ?: 0.0
        val course2Liked = course2.likedRating ?: 0.0
        val course1Easy = course1.easyRating ?: 0.0
        val course2Easy = course2.easyRating ?: 0.0
        val course1Useful = course1.usefulRating ?: 0.0
        val course2Useful = course2.usefulRating ?: 0.0

        val averageRating1 = ((course1Liked * 1).plus(course1Easy * 2).plus(course1Useful * 1)) / 3.0
        val averageRating2 = ((course2Liked * 1).plus(course2Easy * 2).plus(course2Useful * 1)) / 3.0

        averageRating2.compareTo(averageRating1)
    }


    private fun checkConstraint(plannedCourseID: String, parsedDataMap:MutableMap<String, ParsedPrereqData>, majors: List<String>) : Boolean {
        val selectedCourseData = parsedDataMap[plannedCourseID]
        for (major in selectedCourseData!!.notOpenTo) {
            if (major in majors) {
                return false
            }
        }
        var satisfyOnlyOpenTo = false
        for (major in selectedCourseData!!.onlyOpenTo) {
            if (major in majors) {
                satisfyOnlyOpenTo = true
            }
        }
        if (selectedCourseData.onlyOpenTo.size == 0) {
            satisfyOnlyOpenTo = true
        }
        if (!satisfyOnlyOpenTo) {
            return false
        }
        for (antireqCourse in selectedCourseData!!.antireqCourses) {
            if (antireqCourse == "") {
                continue
            }
            if (antireqCourse in listofTakenCourses) {
                return false
            }
        }
        var satisfyPrereq = false
        var satisfyCoreq = false
        for (prereqCourseOption in selectedCourseData.courses) {
            var satisfy = true
            if (prereqCourseOption.size == 0) {
                continue
            }
            for (course in prereqCourseOption) {
                if (course !in listofTakenCourses) {
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

        for (coreqCourseOption in selectedCourseData!!.coreqCourses) {
            var satisfy = true
            if (coreqCourseOption.size == 0) {
                continue
            }
            for (course in coreqCourseOption) {
                if (course !in listofTakenCourses) {
                    satisfy = false
                    break
                }
            }
            if (satisfy) {
                satisfyCoreq = satisfy
            }
        }
        if (selectedCourseData!!.coreqCourses.size == 0 || plannedCourseID == "CS 136" || plannedCourseID == "CS 146" || plannedCourseID == "CS 136L") {
            satisfyCoreq = true
        }
        return !(!satisfyPrereq || !satisfyCoreq)
    }

    private fun selectCoursesFromOptional(
        optionalCourseList: OptionalCourses,
        parsedDataList: MutableMap<String, ParsedPrereqData>,
        majors: List<String>,
        mathCourses: MutableSet<Course>,
        nonMathCourses: MutableSet<Course>
    ) {
        val count = optionalCourseList.nOf
        var curCount = 0
//        var selectedCourses = optionalCourseList.courses.sortedWith(courseComparator).take(optionalCourseList.nOf).toSet()
        var selectedCourses = optionalCourseList.courses.sortedWith(courseComparator).toSet()

        val chosenCourses = mutableListOf<Course>()
        for (course in selectedCourses) {
            val result = checkConstraint(course.courseID, parsedDataList, majors)
            if (result) {
                chosenCourses.add(course)
                listofTakenCourses.add(course.courseID)
                curCount += 1
            }
            if (curCount >= count) {
                break
            }
        }
        selectedCourses = chosenCourses.toSet()

        selectedCourses.forEach {
            if (mathSubjects.contains(it.subject)) {
                mathCourses.add(it)
                countTakenMathCourse++
            } else {
                nonMathCourses.add(it)
                countTakenNonMathCourse++
            }
        }
    }

    private fun selectCommunication(startYear: Year, nonMathCourses: MutableSet<Course>) {
        val sortedList1Courses = communicationRepo.getListNByYear(startYear.toInt(), 1).toList().sortedWith(courseComparator).toMutableList()
        nonMathCourses.add(sortedList1Courses[0])
        countTakenNonMathCourse++
        sortedList1Courses.removeAt(0)
        val sortedComCourses: List<Course> = (sortedList1Courses + communicationRepo.getListNByYear(startYear.toInt(), 2).toList()).sortedWith(courseComparator)
        nonMathCourses.add(sortedComCourses[0])
        countTakenNonMathCourse++
    }

    private fun getCompleteOptionCourse(majors: List<String>) : List<String> {
        var numNonMathNeeded = TOTAL_NON_MATH - countTakenNonMathCourse
        var numMathNeeded = TOTAL_MATH - countTakenMathCourse
        if (numNonMathNeeded < 0) {
            numMathNeeded += numNonMathNeeded
        } else if (numMathNeeded < 0) {
            numNonMathNeeded += numMathNeeded
        }
        if (numMathNeeded <= 0 || numNonMathNeeded <= 0) {
            return listOf()
        }
        val checkMajorMap = mutableMapOf<String, Boolean>()
        val countMajorMap = mutableMapOf<String, Int>()
        for (major in majorMap.keys) {
            checkMajorMap[major] = false
        }
        var neededMajorList = mutableListOf<String>()
        var countMajors = 0
        for (major in majors) {
            if (major in majorMap.keys) {
                checkMajorMap[major] = true
                countMajors++
            }
        }
        if (countMajors == 0) {
            val courses = courseRepo.getBySubject(mathSubjects.toSet())
        }
        return listOf()
    }

    private data class OptionalCourses (
        var nOf: Int = 1,
        val courses: MutableSet<Course> = mutableSetOf()
    )



    fun getCoursesPlanToTake(
        startYear: Year,
        requirements: Requirements,
        majors: List<String>
    ): Pair<MutableSet<Course>, MutableSet<Course>> {
        val mathCourses: MutableSet<Course> = mutableSetOf()
        val nonMathCourses: MutableSet<Course> = mutableSetOf()
        val mandatoryCourses: MutableSet<Course> = courseRepo.getBySubjectCode(requirements.mandatoryCourses)
        val optionalCourses: MutableSet<OptionalCourses> = requirements.optionalCourses.map {
            OptionalCourses(it.nOf, courseRepo.getBySubjectCode(it.courses))
        }.toMutableSet()
        val optionalCoursesID: MutableList<String> = optionalCourses.map{it ->
            it.courses.map{it.courseID}
        }.flatten().toMutableList()
        val modifiedMajors = mutableListOf<String>()

        for (major in majors) {
            if (major.contains("Computer Science")) {
                modifiedMajors.add("Computer Science")
            } else {
                modifiedMajors.add(major)
            }
        }
        modifiedMajors.add("Faculty of Mathematics")

        val parsedDataList = prerequisiteRepo.getParsedPrereqData(optionalCoursesID)

        for (mandatoryCourse in mandatoryCourses) {
            if (mathSubjects.contains(mandatoryCourse.subject)) {
                mathCourses.add(mandatoryCourse)
                countTakenMathCourse++
            } else {
                nonMathCourses.add(mandatoryCourse)
                countTakenNonMathCourse++
            }
            listofTakenCourses.add(mandatoryCourse.courseID)
        }
        for (optionalCourseList in optionalCourses) {
            selectCoursesFromOptional(optionalCourseList, parsedDataList, modifiedMajors, mathCourses, nonMathCourses)
        }

        selectCommunication(startYear, nonMathCourses)
        // Always prefer MATH136 over MATH146
        for (mathCourse in mathCourses) {
            if (mathCourse.courseID == "MATH 146") {
                mathCourses.remove(mathCourse)
                var math136 = courseRepo.getById("MATH 136")
                if (math136 != null) {
                    mathCourses.add(math136)
                    break
                }
            }
        }
        // Always prefer STAT20 over STAT240
        for (mathCourse in mathCourses) {
            if (mathCourse.courseID == "STAT 240") {
                mathCourses.remove(mathCourse)
                var stat230 = courseRepo.getById("STAT 230")
                if (stat230 != null) {
                    mathCourses.add(stat230)
                    break
                }
            }
        }

        // Always prefer MATH136 over MATH146
        for (mathCourse in mathCourses) {
            if (mathCourse.courseID == "MATH 146") {
                mathCourses.remove(mathCourse)
                var math136 = courseRepo.getById("MATH 136")
                if (math136 != null) {
                    mathCourses.add(math136)
                    break
                }
            }
        }
        return Pair(mathCourses, nonMathCourses)
    }
}
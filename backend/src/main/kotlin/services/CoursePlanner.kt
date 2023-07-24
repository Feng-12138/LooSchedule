package services

import entities.Course
import entities.CourseId
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
    private var seasonCourseCounter = mutableMapOf<String, Int>(
        "F" to 0,
        "W" to 0,
        "S" to 0
    )

    private var coursesListMapper = mutableMapOf<String, MutableSet<Course>>(
        "F" to mutableSetOf<Course>(),
        "W" to mutableSetOf<Course>(),
        "S" to mutableSetOf<Course>()
    )


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


    private fun checkConstraint(course: Course, parsedDataMap:MutableMap<String, ParsedPrereqData>, majors: List<String>) : Boolean {
        val selectedCourseData = parsedDataMap[course.courseID]
        for (major in selectedCourseData!!.notOpenTo) {
            if (major in majors) {
                return false
            }
        }
        if (course.courseID in listofTakenCourses) {
            return false
        }
        if (course.availability == "") {
            return false
        }
        val availableSeasonList = seasonCourseCounter.keys.mapNotNull {key ->
            if (seasonCourseCounter[key] !!> 0) key else null
        }
        var satisfyTermConstraint = false
        for (season in course.availability!!) {
            if(season.toString() in availableSeasonList) {
                satisfyTermConstraint = true
            }
        }
        if (!satisfyTermConstraint) {
            return false
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
        if (selectedCourseData!!.coreqCourses.size == 0 || course.courseID == "CS 136" || course.courseID == "CS 146" || course.courseID == "CS 136L") {
            satisfyCoreq = true
        }
        return !(!satisfyPrereq || !satisfyCoreq)
    }

    private fun selectCoursesFromOptional(
        optionalCourseList: OptionalCourses,
        parsedDataList: MutableMap<String, ParsedPrereqData>,
        majors: List<String>,
        mathCourses: MutableSet<Course>,
        nonMathCourses: MutableSet<Course>,

    ) {
        val count = optionalCourseList.nOf
        var curCount = 0
        var selectedCourses = optionalCourseList.courses.sortedWith(courseComparator).toSet()

        val chosenCourses = mutableListOf<Course>()
        for (course in selectedCourses) {
            val result = checkConstraint(course, parsedDataList, majors)
            if (result) {
                chosenCourses.add(course)
                listofTakenCourses.add(course.courseID)
                computeAndUpdateTermCourses(course)
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
        computeAndUpdateTermCourses(sortedList1Courses[0])
        nonMathCourses.add(sortedList1Courses[0])
        countTakenNonMathCourse++
        sortedList1Courses.removeAt(0)
        val sortedComCourses: List<Course> = (sortedList1Courses + communicationRepo.getListNByYear(startYear.toInt(), 2).toList()).sortedWith(courseComparator)
        computeAndUpdateTermCourses(sortedComCourses[0])
        nonMathCourses.add(sortedComCourses[0])
        countTakenNonMathCourse++
    }

    private fun computeAndUpdateTermCourses(course: Course) {
        var maxAvailability = -1
        var bestTerm = ""
        if (course.availability == "") {
            return
        }
        if (course.availability!!.contains("F")) {
            val count = seasonCourseCounter["F"] ?: 0
            if (count > maxAvailability) {
                maxAvailability = count
                bestTerm = "F"
            }
        }
        if (course.availability!!.contains("W")) {
            val count = seasonCourseCounter["W"] ?: 0
            if (count > maxAvailability) {
                maxAvailability = count
                bestTerm = "W"
            }
        }
        if (course.availability!!.contains("S")) {
            val count = seasonCourseCounter["S"] ?: 0
            if (count > maxAvailability) {
                maxAvailability = count
                bestTerm = "S"
            }
        }
        val count = seasonCourseCounter[bestTerm]?: 0
        seasonCourseCounter[bestTerm] = count - 1
        coursesListMapper[bestTerm]?.add(course)
    }

    private fun addCoursesBySubject(subjectSet: Set<String>, included : Boolean = true, countMathCourse: Int,
                                    numMathNeeded: Int,
                                    majors: List<String>, returnedCourseList : List<Course>) : List<Course> {
        var countMathCourse = countMathCourse
        var returnedCourseList = returnedCourseList.toMutableList()
        val courses = courseRepo.getBySubject(mathSubjects.toSet(), included).sortedWith(courseComparator)
        val parsedDataMap = prerequisiteRepo.getParsedPrereqData(courses.map{it.courseID}.take(300))
        var idx = 0
        for (course in courses) {
            if (countMathCourse >= numMathNeeded) {
                break
            }
            idx++
            // error because of the need to get prereq data again
            val result = checkConstraint(course, parsedDataMap = parsedDataMap, majors = majors)
            if (result) {
                computeAndUpdateTermCourses(course)
                returnedCourseList.add(course)
                countMathCourse++
            }
        }
        return returnedCourseList.toList()
    }

    private fun getCompleteOptionCourse(majors: List<String>, parsedDataMap: MutableMap<String, ParsedPrereqData>) : List<Course> {
        var numNonMathNeeded = TOTAL_NON_MATH - countTakenNonMathCourse
        var numMathNeeded = TOTAL_MATH - countTakenMathCourse
        var returnedCourseList = mutableListOf<Course>()
        if (numNonMathNeeded < 0) {
            numMathNeeded += numNonMathNeeded
        } else if (numMathNeeded < 0) {
            numNonMathNeeded += numMathNeeded
        }
        if (numMathNeeded <= 0 || numNonMathNeeded <= 0) {
            return returnedCourseList.toList()
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
        var countMathCourse = 0
        var countNonMathCourse = 0
        countMajors = 0
        if (countMajors == 0) {
            returnedCourseList = addCoursesBySubject(mathSubjects.toSet(), countMathCourse = countMathCourse,
                numMathNeeded = numMathNeeded, returnedCourseList = returnedCourseList, majors = majors).toMutableList()
            returnedCourseList = addCoursesBySubject(mathSubjects.toSet(), countMathCourse = countNonMathCourse,
                numMathNeeded = numNonMathNeeded, returnedCourseList = returnedCourseList, majors = majors, included = false).toMutableList()
        } else {

        }
        return returnedCourseList
    }

    private data class OptionalCourses (
        var nOf: Int = 1,
        val courses: MutableSet<Course> = mutableSetOf()
    )


    fun getCoursesPlanToTake(
        startYear: Year,
        requirements: Requirements,
        majors: List<String>,
        sequenceMap: MutableMap<String, String>
    ): Map<String, MutableSet<Course>> {
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

        // count how many courses for each season:
        for ((key, value) in sequenceMap) {
            val count = seasonCourseCounter[value] ?: 0
            if (key.contains("WT")) {
                seasonCourseCounter[value] = count + 1
            } else {
                seasonCourseCounter[value] = count + 5
            }
        }
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
            if (mandatoryCourse.availability == "") {
                throw Exception("Mandatory course must be valid, check database and requirement")
            }
            computeAndUpdateTermCourses(mandatoryCourse)

            listofTakenCourses.add(mandatoryCourse.courseID)
        }

        for (optionalCourseList in optionalCourses) {
            selectCoursesFromOptional(optionalCourseList, parsedDataList, modifiedMajors.toList(), mathCourses, nonMathCourses)
        }

        selectCommunication(startYear, nonMathCourses)

        val courses = getCompleteOptionCourse(modifiedMajors.toList(), parsedDataList)
        for (course in courses) {
            if (course.subject in mathSubjects) {
                mathCourses.add(course)
            } else {
                nonMathCourses.add(course)
            }
        }
        countTakenMathCourse = 0
        countTakenNonMathCourse = 0
        listofTakenCourses.clear()
        return coursesListMapper
    }
}
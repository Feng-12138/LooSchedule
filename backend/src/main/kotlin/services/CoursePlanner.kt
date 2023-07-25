package services

import entities.Course
import entities.CourseId
import entities.Year
import jakarta.inject.Inject
import repositories.CommunicationRepo
import repositories.CourseRepo
import repositories.ParsedPrereqData
import repositories.PrerequisiteRepo
import kotlin.math.floor

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
        "S" to mutableSetOf<Course>(),
    )

    private var prereqMap : MutableMap<String, ParsedPrereqData> = mutableMapOf<String, ParsedPrereqData>()


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
        if (course.courseID.last() == 'E') {
            return false
        }
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
    ) : Int {
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
        return count - curCount
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
        // Add constraints to some of the first year courses
        if (course.courseID == "MATH 135" || course.courseID == "CS 135" || course.courseID == "MATH 145"
            || course.courseID == "MATH 137" || course.courseID == "CS 115"
            || course.courseID == "MATH 147" || course.courseID == "CS 145" || course.courseID in ListOneCourses) {
            val count = seasonCourseCounter["F"]?: 0
            seasonCourseCounter["F"] = count - 1
            coursesListMapper["F"]?.add(course)
            return
        }

        if (course.courseID == "MATH 136" || course.courseID == "MATH 146" || course.courseID == "MATH 138" ||
            course.courseID == "MATH 148" || course.courseID == "CS 136" || course.courseID == "CS 116"
            || course.courseID == "CS 146" || course.courseID == "STAT 230" || course.courseID == "STAT 240") {
            val count = seasonCourseCounter["W"]?: 0
            seasonCourseCounter["W"] = count - 1
            coursesListMapper["W"]?.add(course)
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
                                    majors: List<String>, returnedCourseList : List<Course>, item: Int = 1) {
        var countMathCourse = countMathCourse
        var returnedCourseList = returnedCourseList.toMutableList()
        val courses = courseRepo.getBySubject(subjectSet, included).sortedWith(courseComparator)
        val parsedDataMap = prerequisiteRepo.getParsedPrereqData(courses.map{it.courseID}.take(350))
        prereqMap.putAll(parsedDataMap)
        var idx = 0
        for (course in courses) {
            if (countMathCourse >= numMathNeeded) {
                break
            }
            idx++
            val result = checkConstraint(course, parsedDataMap = parsedDataMap, majors = majors)
            if (result) {
                computeAndUpdateTermCourses(course)
                returnedCourseList.add(course)
                countMathCourse++
            }
        }
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
        var countMajors = 0
        for (major in majors) {
            if (major in majorMap.keys) {
                countMajors++
            }
        }
        var countMathCourse = 0
        var countNonMathCourse = 0
        if (countMajors == 0) {
            addCoursesBySubject(mathSubjects.toSet(), countMathCourse = countMathCourse,
                numMathNeeded = numMathNeeded, returnedCourseList = returnedCourseList, majors = majors)
            addCoursesBySubject(mathSubjects.toSet(), countMathCourse = countNonMathCourse,
                numMathNeeded = numNonMathNeeded, returnedCourseList = returnedCourseList, majors = majors, included = false)
        } else {
            val numMajorCourse = floor(numMathNeeded * 0.9).toInt()
            var subjectCodeList = mutableListOf<String>()
            for (major in majors) {
                if (major in majorMap.keys) {
                    subjectCodeList.add(majorMap[major]!!)
                }
            }
            addCoursesBySubject(subjectCodeList.toSet(), countMathCourse = countMathCourse,
                numMathNeeded = numMajorCourse, returnedCourseList = returnedCourseList, majors = majors)
            addCoursesBySubject(mathSubjects.filter { it !in subjectCodeList}.toSet(), countMathCourse = 0,
                numMathNeeded = numMathNeeded - numMajorCourse, returnedCourseList = returnedCourseList, majors = majors, item = 2)
            addCoursesBySubject(mathSubjects.toSet(), countMathCourse = countNonMathCourse,
                numMathNeeded = numNonMathNeeded, returnedCourseList = returnedCourseList, majors = majors, included = false, item = 3)
        }
        return returnedCourseList
    }

    private data class OptionalCourses (
        var nOf: Int = 1,
        val courses: MutableSet<Course> = mutableSetOf()
    )

    fun getPrereqMap() : MutableMap<String, ParsedPrereqData>{
        return prereqMap
    }


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
        optionalCoursesID.addAll(mandatoryCourses.map { it.courseID })
        optionalCoursesID.addAll(listofTakenCourses)
        val parsedDataList = prerequisiteRepo.getParsedPrereqData(optionalCoursesID)
        prereqMap.putAll(parsedDataList)

        var mandatoryCourseNonMath = mutableListOf<Course>()
        for (mandatoryCourse in mandatoryCourses) {
            if (mathSubjects.contains(mandatoryCourse.subject)) {
                mathCourses.add(mandatoryCourse)
                computeAndUpdateTermCourses(mandatoryCourse)
                countTakenMathCourse++
            } else {
                nonMathCourses.add(mandatoryCourse)
                mandatoryCourseNonMath.add(mandatoryCourse)
                countTakenNonMathCourse++
            }
            if (mandatoryCourse.availability == "") {
                throw Exception("Mandatory course must be valid, check database and requirement")
            }
            listofTakenCourses.add(mandatoryCourse.courseID)
        }

        var incompleteOptionalList = mutableSetOf<OptionalCourses>()
        for (optionalCourseList in optionalCourses) {
            val result = selectCoursesFromOptional(optionalCourseList, parsedDataList, modifiedMajors.toList(), mathCourses, nonMathCourses)
            if (result > 0) {
                incompleteOptionalList.add(optionalCourseList.copy(courses = optionalCourseList.courses, nOf = result))
            }
        }
        for (optionalCourseList in incompleteOptionalList) {
            selectCoursesFromOptional(optionalCourseList, parsedDataList, modifiedMajors.toList(), mathCourses, nonMathCourses)
        }

        for (course in mandatoryCourseNonMath) {
            computeAndUpdateTermCourses(course)
        }
        selectCommunication(startYear, nonMathCourses)
        // non math courses are more flexible

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
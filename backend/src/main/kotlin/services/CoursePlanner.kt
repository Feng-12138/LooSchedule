package services

import entities.Course
import entities.Year
import jakarta.inject.Inject
import repositories.CommunicationRepo
import repositories.CourseRepo
import repositories.ParsedPrereqData
import repositories.PrerequisiteRepo
import kotlin.math.floor

const val TOTAL_COURSES = 45
const val TOTAL_MATH = 33
const val TOTAL_NON_MATH = 12
class CoursePlanner {
    @Inject
    private lateinit var courseRepo: CourseRepo

    @Inject
    private lateinit var communicationRepo: CommunicationRepo

    @Inject
    private lateinit var prerequisiteRepo: PrerequisiteRepo

    private val mathSubjects = listOf("MATH", "STAT", "CS", "CO", "ACTSC", "AMATH", "PMATH")

    private val listOneCourses = listOf("COMMST 100", "COMMST 223", "EMLS 101R", "EMLS 102R", "EMLS 129R", "ENGL 129R", "ENGL 109")

    private val majorMap = mapOf("Statistics" to "STAT", "Computer Science" to "CS", "Actuarial Science" to "ACTSC",
        "Applied Mathematics" to "AMATH", "Combinatorics and Optimization" to "CO", "Pure Mathematics" to "PMATH")


    private val listofTakenCourses = mutableListOf<String>()
    private val takenCoursesPrereq : MutableMap<String, ParsedPrereqData> = mutableMapOf()

    private var countTakenNonMathCourse = 0
    private var countTakenMathCourse = 0
    private var seasonCourseCounter = mutableMapOf(
        "F" to 0,
        "W" to 0,
        "S" to 0
    )

    private var coursesListMapper: Map<String, MutableSet<Course>> = mutableMapOf(
        "F" to mutableSetOf(),
        "W" to mutableSetOf(),
        "S" to mutableSetOf(),
    )

    private var prereqMap : MutableMap<String, ParsedPrereqData> = mutableMapOf()


    private val courseComparator = Comparator<Course> { course1, course2 ->
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
        if (selectedCourseData == null) {
            return false
        }
        if (course.courseID.last() == 'E') {
            return false
        }
        for (major in selectedCourseData.notOpenTo) {
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
            if (seasonCourseCounter[key]!! > 0) key else null
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
        for (major in selectedCourseData.onlyOpenTo) {
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
        for (antireqCourse in selectedCourseData.antireqCourses) {
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
            for (preReqCourse in prereqCourseOption) {
                if (preReqCourse !in listofTakenCourses) {
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
                if (coReqCourse !in listofTakenCourses) {
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
                course.color = "red"
                if (course.subject in mathSubjects) {
                    course.priorityPoint = 6
                } else {
                    course.priorityPoint = 3
                }
                course.priorityPoint = 6
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

    private fun selectCommunication(
        startYear: Year,
        nonMathCourses: MutableSet<Course>,
        takenCourses: List<String>,
    ) {
        val sortedList1Courses = communicationRepo.getListNByYear(startYear.toInt(), 1).toList().sortedWith(courseComparator).toMutableList()
        sortedList1Courses[0].color = "green"
        sortedList1Courses[0].priorityPoint = 2
        if (!sortedList1Courses.any { it.subject + " " + it.courseID in takenCourses }) {
            computeAndUpdateTermCourses(sortedList1Courses[0])
            countTakenNonMathCourse++
        }
        nonMathCourses.add(sortedList1Courses[0])
        sortedList1Courses.removeAt(0)
        val sortedComCourses: List<Course> = (sortedList1Courses + communicationRepo.getListNByYear(startYear.toInt(), 2).toList()).sortedWith(courseComparator)
        sortedComCourses[0].color = "green"
        sortedComCourses[0].priorityPoint = 2
        countTakenNonMathCourse++
        if (!sortedComCourses.any { it.subject + " " + it.courseID in takenCourses }) {
            computeAndUpdateTermCourses(sortedComCourses[0])
            countTakenNonMathCourse++
        }
        nonMathCourses.add(sortedComCourses[0])
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
            || course.courseID == "MATH 147" || course.courseID == "CS 145" || course.courseID in listOneCourses) {
            val count = seasonCourseCounter["F"]?: 0
            seasonCourseCounter["F"] = count - 1
            coursesListMapper["F"]?.add(course)
            return
        }

        if (course.courseID == "MATH 136" || course.courseID == "MATH 146" || course.courseID == "MATH 138" ||
            course.courseID == "MATH 148" || course.courseID == "CS 136" || course.courseID == "CS 116"
            || course.courseID == "CS 146" || course.courseID == "STAT 230") {
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
                bestTerm = "S"
            }
        }
        val count = seasonCourseCounter[bestTerm]?: 0
        seasonCourseCounter[bestTerm] = count - 1
        coursesListMapper[bestTerm]?.add(course)
    }

    private fun addCoursesBySubject(subjectSet: Set<String>, included : Boolean = true,
                                    countMathCourse: Int, numMathNeeded: Int,
                                    majors: List<String>, returnedCourseList : List<Course>, item: Int = 1) {
        var mathCourseCount = countMathCourse
        val mutableReturnedCourseList = returnedCourseList.toMutableList()
        val courses = courseRepo.getBySubject(subjectSet, included).sortedWith(courseComparator)
        val parsedDataMap = prerequisiteRepo.getParsedPrereqData(courses.map{it.courseID}.take(420))
        prereqMap.putAll(parsedDataMap)
        var idx = 0
        for (course in courses) {
            if (mathCourseCount >= numMathNeeded) {
                break
            }
            idx++
            val result = checkConstraint(course, parsedDataMap = parsedDataMap, majors = majors)
            if (result) {
                if (course.subject in mathSubjects) {
                    course.priorityPoint = 2
                } else {
                    course.priorityPoint = 1
                }
                course.color = "blue"
                computeAndUpdateTermCourses(course)
                mutableReturnedCourseList.add(course)
                mathCourseCount++
            }
        }
    }

    private fun getCompleteOptionCourse(majors: List<String>) : List<Course> {
        var numNonMathNeeded = TOTAL_NON_MATH - countTakenNonMathCourse
        var numMathNeeded = TOTAL_MATH - countTakenMathCourse
        val returnedCourseList = mutableListOf<Course>()
        if (numNonMathNeeded < 0) {
            numMathNeeded += numNonMathNeeded
            numNonMathNeeded = 0
        } else if (numMathNeeded < 0) {
            numNonMathNeeded += numMathNeeded
            numMathNeeded = 0
        }
        if (numMathNeeded <= 0 && numNonMathNeeded <= 0) {
            return listOf()
        }
        var countMajors = 0
        for (major in majors) {
            if (major in majorMap.keys) {
                countMajors++
            }
        }
        val countMathCourse = 0
        val countNonMathCourse = 0
        if (countMajors == 0) {
            addCoursesBySubject(mathSubjects.toSet(), countMathCourse = countMathCourse,
                numMathNeeded = numMathNeeded, returnedCourseList = returnedCourseList, majors = majors)
            addCoursesBySubject(mathSubjects.toSet(), countMathCourse = countNonMathCourse,
                numMathNeeded = numNonMathNeeded, returnedCourseList = returnedCourseList, majors = majors, included = false)
        } else {
            val numMajorCourse = floor(numMathNeeded * 0.9).toInt()
            val subjectCodeList = mutableListOf<String>()
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
        sequenceMap: MutableMap<String, String>,
        takenCourses: List<String> = listOf(),
        recommendedCourses: Set<Course> = setOf(),
    ): Map<String, MutableSet<Course>> {
        seasonCourseCounter["F"] = 0
        seasonCourseCounter["W"] = 0
        seasonCourseCounter["S"] = 0
        val mathCourses: MutableSet<Course> = mutableSetOf()
        val nonMathCourses: MutableSet<Course> = mutableSetOf()
        listofTakenCourses.addAll(takenCourses)
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
        println(seasonCourseCounter["F"])
        println(seasonCourseCounter["W"])
        println(seasonCourseCounter["S"])



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

        val mandatoryCourseNonMath = mutableListOf<Course>()
        for (mandatoryCourse in mandatoryCourses) {
            mandatoryCourse.color = "red"
            mandatoryCourse.priorityPoint = 6
            if (mathSubjects.contains(mandatoryCourse.subject)) {
                mathCourses.add(mandatoryCourse)
                computeAndUpdateTermCourses(mandatoryCourse)
                countTakenMathCourse++
            } else {
                mandatoryCourse.priorityPoint = 3
                nonMathCourses.add(mandatoryCourse)
                mandatoryCourseNonMath.add(mandatoryCourse)
                countTakenNonMathCourse++
            }
            if (mandatoryCourse.availability == "") {
                throw Exception("Mandatory course must be valid, check database and requirement")
            }
            listofTakenCourses.add(mandatoryCourse.courseID)
        }

        takenCourses.forEach {
            val temp = it.split(" ")
            val subject = temp[0]
            val code = temp[1]
            val course = courseRepo.getBySubjectCode(setOf(Course(subject, code))).single()
            if (mathSubjects.contains(it.split(" ")[0])) {
                mathCourses.add(course)
                countTakenMathCourse++
            } else {
                mathCourses.add(course)
                countTakenNonMathCourse++
            }
        }

        // select courses from optional courses requirements
        val incompleteOptionalList = mutableSetOf<OptionalCourses>()
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
            course.color = "red"
            computeAndUpdateTermCourses(course)
        }

        selectCommunication(startYear, nonMathCourses, takenCourses)

        // consider recommended courses
//        val takeRecommendedCoursesList = mutableListOf<Course>()

        val parsedDataMap = prerequisiteRepo.getParsedPrereqData(recommendedCourses.map { it.courseID })
        prereqMap.putAll(parsedDataMap)


        for (course in recommendedCourses) {
            val (takenCount, neededCount) = when (course.subject) {
                in mathSubjects -> countTakenMathCourse to TOTAL_MATH
                else -> countTakenNonMathCourse to TOTAL_NON_MATH
            }
            if (takenCount >= neededCount) continue

            val result = checkConstraint(course, parsedDataMap = parsedDataMap, majors = modifiedMajors.toList())
            if (result) {
                course.color = "purple"
                course.priorityPoint = 3
                computeAndUpdateTermCourses(course)
//                takeRecommendedCoursesList.add(course)
                listofTakenCourses.add(course.courseID)
                if (course.subject in mathSubjects) {
                    countTakenMathCourse++
                    mathCourses.add(course)
                } else {
                    countTakenNonMathCourse++
                    nonMathCourses.add(course)
                }
            }
        }
//
//        println("aaaaaaaaaaaa")
//        println(takeRecommendedCoursesList.map { it.courseID })
//        println("aaaaaaaaaaaaa")

        // select actually elective courses
        // non math courses are more flexible
        val courses = getCompleteOptionCourse(modifiedMajors.toList())
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
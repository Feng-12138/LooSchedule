package services
import entities.Course
import repositories.ParsedPrereqData
import repositories.PrerequisiteRepo
import jakarta.inject.Inject
import repositories.CourseRepo
import java.util.TooManyListenersException

class TermMapperService {
    @Inject
    private lateinit var prerequisiteRepo: PrerequisiteRepo

    @Inject
    private lateinit var courseRepo: CourseRepo

    @Inject
    private lateinit var coursePlanner: CoursePlanner

    private var takeCourseInWT = false
    private var takenCourses : MutableList<String> = mutableListOf()

    fun mapCoursesToSequence(
        courseData: CourseDataClass,
        sequenceMap: Map<String, String>,
        currentTerm: String
    ): MutableMap<String, MutableList<Course>> {
        val list = courseData.mathCourses.map { it.courseID }.toMutableList()
        list.addAll(courseData.nonMathCourses.map { it.courseID })
        val prereqsData = prerequisiteRepo.getParsedPrereqData(list)
        val countCourseTerm = mutableMapOf<String, Int>()
        val generatedSchedule = mutableMapOf<String, MutableList<Course>>()
        val totalNumberCourses = courseData.nonMathCourses.size + courseData.mathCourses.size
        println(courseData.mathCourses.map { it.courseID })
        var coursePerTerm : Int
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
        for ((key, value) in prereqsData) {
            if (key == "CO 250") {
                value.courses = mutableListOf(mutableListOf("MATH 136"), mutableListOf("MATH 146"))
            }
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
            val courseList = generateCourseForTerm(
                mathCourse = courseData.mathCourses,
                nonMathCourse = courseData.nonMathCourses,
                numCourse = countCourseTerm[key]!!.toInt(),
                season = value,
                prereqMap = prereqsData,
                termName = key,
            )
            val coursesTakeThisTerm = courseList.map{it.courseID}
            takenCourses.addAll(coursesTakeThisTerm)
            generatedSchedule[key] = courseList
        }
//        var finalSchedule: MutableMap<String, MutableList<Course>> = finalizeSchedule(generatedSchedule, countCourseTerm, sequenceMap, prereqsData)
        takenCourses.clear()
        return generatedSchedule
    }

    private fun checkMathCourseConstraint(course: Course, termName : String, season: String,
                                          prereqMap: MutableMap<String, ParsedPrereqData>): Boolean {
        val parsedPrereqData = prereqMap[course.courseID]
        assert(parsedPrereqData != null)
        var satisfyTerm = false
        if (course.availability!!.contains(season) && parsedPrereqData!!.minimumLevel <= termName) {
            satisfyTerm = true
        }

        if (parsedPrereqData!!.courses.isEmpty() || parsedPrereqData.courses.all {it.isEmpty()}) {
            return true
        } else {
            for (requirement in parsedPrereqData.courses) {
                var satisfyPrereq = true
                for (prereqCourse in requirement) {
                    if (prereqCourse !in takenCourses) {
                        satisfyPrereq = false
                        break
                    }
                }
                if (satisfyPrereq && satisfyTerm) {
                    return true
                }
            }
            return false
        }
    }

    enum class NonMathAddStatus {
        AddToOnline,
        AddToInPerson,
        DontAdd,
    }
    private fun checkNonMathCourseConstraint(course: Course, termName : String, season: String,
                                             prereqMap: MutableMap<String, ParsedPrereqData>): NonMathAddStatus {
        val parsedPrereqData = prereqMap[course.courseID]
        assert(parsedPrereqData != null)
        var satisfyTerm = false
        if (course.availability!!.contains(season) && parsedPrereqData!!.minimumLevel <= termName) {
            satisfyTerm = true
        }
        if (parsedPrereqData!!.courses.isEmpty() || parsedPrereqData.courses.all {it.isEmpty()}) {
            return NonMathAddStatus.AddToOnline
        } else {
            for (requirement in parsedPrereqData.courses) {
                var satisfyPrereq = true
                for (prereqCourse in requirement) {
                    if (prereqCourse !in takenCourses) {
                        satisfyPrereq = false
                        break
                    }
                }
                if (satisfyPrereq && satisfyTerm && course.onlineTerms!!.contains(season)) {
                    return NonMathAddStatus.AddToOnline
                } else if (satisfyPrereq && satisfyTerm) {
                    return NonMathAddStatus.AddToInPerson
                }
            }
            return NonMathAddStatus.DontAdd
        }
    }

    private fun generateCourseForTerm(
        termName : String,
        season: String,
        numCourse: Int,
        mathCourse: MutableSet<Course>,
        nonMathCourse: MutableSet<Course>,
        prereqMap: MutableMap<String, ParsedPrereqData>
    ) : MutableList<Course> {
        // non math courses have not taken
        val notTakenNonMathCourse = mutableListOf<Course>()
        val notTakenMathCourse = mutableListOf<Course>()
        var satisfyConstraintMathCourse = mutableListOf<Course>()
        // non math courses could be taken this term
        var satisfyConstraintNonMathCourse = mutableListOf<Course>()
        // non math courses could be taken this term online, DOES NOT DUPLICATE WITH satisfyConstraintNonMathCourse
        var satisfyConstraintOnlineNonMathCourse = mutableListOf<Course>()
        val retvalList = mutableListOf<Course>()
        for (course in mathCourse) {
            if (course.courseID !in takenCourses) {
                notTakenMathCourse.add(course)
            }
        }
        for (course in nonMathCourse) {
            if (course.courseID !in takenCourses) {
                notTakenNonMathCourse.add(course)
            }
        }

        for (course in notTakenMathCourse) {
            if (checkMathCourseConstraint(course, termName, season, prereqMap)) {
                satisfyConstraintMathCourse.add(course)
            }
        }

        for (course in notTakenNonMathCourse) {
            val addStatus: NonMathAddStatus = checkNonMathCourseConstraint(course, termName, season, prereqMap)
            if (addStatus == NonMathAddStatus.AddToOnline) {
                satisfyConstraintOnlineNonMathCourse.add(course)
            } else if (addStatus == NonMathAddStatus.AddToInPerson) {
                satisfyConstraintNonMathCourse.add(course)
            }
        }
        var newSatisfyConstraintMathCourse = satisfyConstraintMathCourse.sortedBy { it.courseID }
        var newSatisfyConstraintNonMathCourse = satisfyConstraintNonMathCourse.sortedBy { it.courseID }
        var newSatisfyConstraintOnlineNonMathCourse = satisfyConstraintOnlineNonMathCourse.sortedBy { it.courseID }
        var numCourseCounter = numCourse
        for (i in 0 until numCourse - 1) {
            if (i >= newSatisfyConstraintMathCourse.size) {
                break
            } else {
                retvalList.add(newSatisfyConstraintMathCourse[i])
            }
            numCourseCounter--
        }
        var i = 0

        while (i < numCourseCounter) {
            var counter = i
            if (termName.contains("WT")) {
                for (item in newSatisfyConstraintOnlineNonMathCourse) {
                    retvalList.add(item)
                    counter++
                    if (counter >= numCourseCounter) {
                        break
                    }
                }
                if (counter >= numCourseCounter) {
                    break
                }
                for (item in newSatisfyConstraintNonMathCourse) {
                    retvalList.add(item)
                    counter++
                    if (counter >= numCourseCounter) {
                        break
                    }
                }
                i = counter
            } else {
                for (item in newSatisfyConstraintNonMathCourse) {
                    retvalList.add(item)
                    counter++
                    if (counter >= numCourseCounter) {
                        break
                    }
                }
                if (counter >= numCourseCounter) {
                    break
                }
                for (item in newSatisfyConstraintOnlineNonMathCourse) {
                    retvalList.add(item)
                    counter++
                    if (counter >= numCourseCounter) {
                        break
                    }
                }
                // 没变
                if (counter < numCourseCounter) {
                    break
                }
                i = counter
            }
        }
        println(retvalList.map{it.courseID})
        return retvalList
    }

    private fun finalizeSchedule(scheduleSoFar: MutableMap<String, MutableList<Course>>,
                                 countCourseTerm: MutableMap<String, Int>, sequenceMap: Map<String, String>,
                                 prereqMap: MutableMap<String, ParsedPrereqData>): MutableMap<String, MutableList<Course>> {
        var availableCourses: MutableList<Course> = courseRepo.getAll().sortedWith(coursePlanner.courseComparator).toMutableList()
        for ((term, schedule) in scheduleSoFar) {
            // Remove taken courses so that no retake
            availableCourses.removeAll(schedule)
            var unfillCourseCount = countCourseTerm[term]!! - schedule.size
            println("Need to have: " + countCourseTerm[term].toString())
            println("have: " + schedule.size.toString())
            println("Need to add: $unfillCourseCount")

            for (availableCourse in availableCourses) {
                var canAddCourse: Boolean
                println("Considering: ${availableCourse.courseID}")
                if (coursePlanner.mathSubjects.contains(availableCourse.subject)) {
                    canAddCourse = checkMathCourseConstraint(availableCourse, term, sequenceMap[term]!!, prereqMap)
                    println("math")
                } else {
                    canAddCourse = checkNonMathCourseConstraint(availableCourse, term, sequenceMap[term]!!, prereqMap) != NonMathAddStatus.DontAdd
                    println("Nonmath")
                }
                if (canAddCourse) {
                    scheduleSoFar[term]!!.add(availableCourse)
                    takenCourses.add(availableCourse.courseID)
                    println("Added: " + availableCourse.courseID)
                    unfillCourseCount--
                    if (unfillCourseCount <= 0) break
                }
                println("Did we add? $canAddCourse")
            }
        }
        return scheduleSoFar
    }
}

data class CourseDataClass(
    var numOfCoursesPerTerm: Int = 5,
    var takeCourseInWT: Boolean = false,
    var mathCourses: MutableSet<Course> = mutableSetOf(),
    var nonMathCourses: MutableSet<Course> = mutableSetOf(),
)

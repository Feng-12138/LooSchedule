package services
import entities.Course
import repositories.ParsedPrereqData
import repositories.PrerequisiteRepo
import jakarta.inject.Inject

class TermMapperService {
    @Inject
    private lateinit var prerequisiteRepo: PrerequisiteRepo

    private var takeCourseInWT = false
    private var takenCourses : MutableList<String> = mutableListOf()

    fun mapCoursesToSequence(courseData: CourseDataClass, sequenceMap: Map<String, String>) : MutableMap<String, List<Course>> {
        val list = courseData.mathCourses.map { it.courseID }.toMutableList()
        list.addAll(courseData.nonMathCourses.map { it.courseID })
        val prereqsData = prerequisiteRepo.getParsedPrereqData(list)
        val countCourseTerm = mutableMapOf<String, Int>()
        val generatedSchedule = mutableMapOf<String, List<Course>>()
        val totalNumberCourses = courseData.nonMathCourses.size + courseData.mathCourses.size
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
        coursePerTerm = 5
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
        takenCourses.clear()
        return generatedSchedule
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
//        if (termName == "4A") {
//            println("here")
//            println(notTakenMathCourse.map { it.courseID })
//            println(notTakenNonMathCourse.map { it.courseID })
//        }

        for (course in notTakenMathCourse) {
            val parsedPrereqData = prereqMap[course.courseID]
            if (parsedPrereqData != null) {
                var satisfyTerm = false
                if (course.availability!!.contains(season)
                    && parsedPrereqData.minimumLevel <= termName) {
                    satisfyTerm = true
//                    if (course.courseID == "MATH 146") {
//                        println(course.availability)
//                        println(season)
//                        println(termName)
//                        println("here")
//                    }
                }
                if (parsedPrereqData.courses.isEmpty() || parsedPrereqData.courses.all {it.isEmpty()}) {
                    satisfyConstraintMathCourse.add(course)
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
                            satisfyConstraintMathCourse.add(course)
                            break
                        }
                    }
                }
            }
        }

        for (course in notTakenNonMathCourse) {
            val parsedPrereqData = prereqMap[course.courseID]
            if (parsedPrereqData != null) {
                var satisfyTerm = false
                if (course.availability!!.contains(season)
                    && parsedPrereqData.minimumLevel <= termName) {
                    satisfyTerm = true
                }
                if (parsedPrereqData.courses.isEmpty() || parsedPrereqData.courses.all {it.isEmpty()}) {
                    satisfyConstraintOnlineNonMathCourse.add(course)
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
                            satisfyConstraintOnlineNonMathCourse.add(course)
                            break
                        } else if (satisfyPrereq && satisfyTerm) {
                            satisfyConstraintNonMathCourse.add(course)
                            break
                        }
                    }
                }
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
}


data class CourseDataClass(
    var numOfCoursesPerTerm: Int = 5,
    var takeCourseInWT: Boolean = false,
    var mathCourses: MutableSet<Course> = mutableSetOf(),
    var nonMathCourses: MutableSet<Course> = mutableSetOf(),
)

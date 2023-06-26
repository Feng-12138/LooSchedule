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
//        println(";;;;;;;;;;;;;;;")
//        println(prereqsData)
        val countCourseTerm = mutableMapOf<String, Int>()
        val generatedSchedule = mutableMapOf<String, List<Course>>()
        val totalNumberCourses = courseData.nonMathCourses.size + courseData.mathCourses.size
        val coursePerTerm : Int
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
        for ((key, _) in sequenceMap) {
            if (key.contains("WT")) {
                if (courseData.takeCourseInWT) {
                    countCourseTerm[key] = 0
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
            println(courseList.map{it.courseID})
            val coursesTakeThisTerm = courseList.map{it.courseID}
            takenCourses.addAll(coursesTakeThisTerm)
            generatedSchedule[key] = courseList
        }

        println("---------")
        println(generatedSchedule)
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
        val satisfyConstraintMathCourse = mutableListOf<Course>()
        // non math courses could be taken this term
        val satisfyConstraintNonMathCourse = mutableListOf<Course>()
        // non math courses could be taken this term online, DOES NOT DUPLICATE WITH satisfyConstraintNonMathCourse
        val satisfyConstraintOnlineNonMathCourse = mutableListOf<Course>()
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
            val parsedPrereqData = prereqMap[course.courseID]
            if (parsedPrereqData != null) {
                var satisfyTerm = false
                if (course.availability!!.contains(season)
                    && parsedPrereqData.minimumLevel <= termName) {
                    satisfyTerm = true
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

        var numCourseCounter = numCourse
        for (i in 0 until numCourse - 1) {
            if (i - 1 >= satisfyConstraintMathCourse.size) {
                break
            } else {
                retvalList.add(satisfyConstraintMathCourse[i])
            }
            numCourseCounter--
        }
        var i = 0

        while (i < numCourseCounter) {
            var counter = i
            if (termName.contains("WT")) {
                for (item in satisfyConstraintOnlineNonMathCourse) {
                    retvalList.add(item)
                    counter++
                    if (counter >= numCourseCounter) {
                        break
                    }
                }
                for (item in satisfyConstraintNonMathCourse) {
                    retvalList.add(item)
                    counter++
                    if (counter >= numCourseCounter) {
                        break
                    }
                }
                i = counter
            } else {
                for (item in satisfyConstraintNonMathCourse) {
                    retvalList.add(item)
                    counter++
                    if (counter >= numCourseCounter) {
                        break
                    }
                }
                for (item in satisfyConstraintOnlineNonMathCourse) {
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

        return retvalList
    }
}


data class CourseDataClass(
    var numOfCoursesPerTerm: Int = 5,
    var takeCourseInWT: Boolean = false,
    var mathCourses: MutableSet<Course> = mutableSetOf(),
    var nonMathCourses: MutableSet<Course> = mutableSetOf(),
)

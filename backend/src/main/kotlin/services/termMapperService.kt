package services
import entities.Course
import repositories.ParsedPrereqData
import repositories.PrerequisiteRepo
import javax.inject.Inject
import kotlin.math.floor


data class course(
    var availability: String = "",
    var courses : MutableList<MutableList<String>> = mutableListOf(),
)

data class courseDataClass(
    var numOfCoursesPerTerm: Int = 5,
    var takeCourseInWT: Boolean = false,
    var mathCourses: MutableSet<Course> = mutableSetOf(),
    var nonMathCourses: MutableSet<Course> = mutableSetOf(),
)

data class prereqDataClass(
    var courseName: String = "",
    var coursesString: String = "",
    var minimumLevel: String = ""
)

class termMapperService() {
    @Inject
    private lateinit var prerequisiteRepo: PrerequisiteRepo
    private var takeCourseInWT = false

    private var takenCourses : MutableList<String> = mutableListOf()

    fun generateCourseForTerm(termName : String, season: String, numCourse: Int, mathCourse: MutableSet<Course>, nonMathCourse: MutableSet<Course>, prereqMap: MutableMap<String, ParsedPrereqData>) : MutableList<Course> {
        val notTakenNonMathCourse = mutableListOf<Course>()
        val notTakenMathCourse = mutableListOf<Course>()
        val satisfyConstraintMathCourse = mutableListOf<Course>()
        val satisfyConstraintNonMathCourse = mutableListOf<Course>()
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
                for (requirement in parsedPrereqData.courses) {
                    var satisfy = true
                    for (prereqCourse in requirement) {
                        if (prereqCourse !in takenCourses && course.availability!!.contains(season) && parsedPrereqData.minimumLevel <= termName) {
                            satisfy = false
                            break
                        }
                    }
                    if (satisfy) {
                        satisfyConstraintMathCourse.add(course)
                    }
                }
            }
        }
        for (course in notTakenNonMathCourse) {
            val parsedPrereqData = prereqMap[course.courseID]
            if (parsedPrereqData != null) {
                for (requirement in parsedPrereqData.courses) {
                    var satisfy = true
                    for (prereqCourse in requirement) {
                        if (prereqCourse !in takenCourses && course.availability!!.contains(season) && parsedPrereqData.minimumLevel <= termName) {
                            satisfy = false
                            break
                        }
                    }
                    if (satisfy && course.onlineTerms!!.contains(season)) {
                        satisfyConstraintOnlineNonMathCourse.add(course)
                    } else if (satisfy) {
                        satisfyConstraintNonMathCourse.add(course)
                    }
                }
            }
        }
        var numCourseCounter = numCourse
        for (i in 1 until numCourse - 1) {
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

    fun mapCoursesToSequence(courseData: courseDataClass, sequenceMap: Map<String, String>) : MutableMap<String, List<Course>> {
        val list = courseData.mathCourses.map { it.courseID }.toMutableList()
        list.addAll(courseData.nonMathCourses.map { it.courseID })
        val prereqsData = prerequisiteRepo.getParsedPrereqData(list)
        var countCourseTerm = mutableMapOf<String, Int>()
        var generatedSchedule = mutableMapOf<String, List<Course>>()
        var totalNumberCourses = courseData.nonMathCourses.size + courseData.mathCourses.size
        var coursePerTerm : Int = 0
        var remainder = 0
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
        for ((key, value) in sequenceMap) {
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
            val courseList = generateCourseForTerm(mathCourse = courseData.mathCourses, nonMathCourse = courseData.nonMathCourses, numCourse = countCourseTerm[key]!!.toInt(), season = value, prereqMap = prereqsData, termName = key)
            generatedSchedule[key] = courseList
        }
        return generatedSchedule
    }
}
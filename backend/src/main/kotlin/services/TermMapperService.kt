package services
import entities.Course
import repositories.ParsedPrereqData
import repositories.PrerequisiteRepo
import jakarta.inject.Inject
import repositories.CourseRepo

class TermMapperService {
    @Inject
    private lateinit var prerequisiteRepo: PrerequisiteRepo

    @Inject
    private lateinit var courseRepo: CourseRepo

    @Inject
    private lateinit var coursePlanner: CoursePlanner

    private var takeCourseInWT = false
    private var takenCourses : MutableList<String> = mutableListOf()

    val ListOneCourses = listOf("COMMST 100", "COMMST 223", "EMLS 101R", "EMLS 102R", "EMLS 129R", "ENGL 129R", "ENGL 109")

    fun mapCoursesToSequence(courseData: CourseDataClass, sequenceMap: Map<String, String>, currentTerm: String): MutableMap<String, MutableList<Course>> {
        val list = courseData.fallCourses.map { it.courseID }.toMutableList()
        list.addAll(courseData.winterCourses.map { it.courseID })
        list.addAll(courseData.springCourses.map{it.courseID})
        var prereqsData = courseData.prereqMap.filterKeys { it in list }
        val countCourseTerm = mutableMapOf<String, Int>()
        val generatedSchedule = mutableMapOf<String, MutableList<Course>>()
        val totalNumberCourses = courseData.fallCourses.size + courseData.winterCourses.size + courseData.springCourses.size
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
            val courseList = generateCourseForTermSeason(season = value, parsedPrereqDataMap = prereqsData.toMutableMap(), fallCourses = courseData.fallCourses,
                springCourses = courseData.springCourses, winterCourses = courseData.winterCourses, numCourse = countCourseTerm[key]!!.toInt(), termName = key)
            val coursesTakeThisTerm = courseList.map{it.courseID}
            takenCourses.addAll(coursesTakeThisTerm)
            generatedSchedule[key] = courseList
        }
        takenCourses.clear()
        courseData.fallCourses.clear()
        courseData.springCourses.clear()
        courseData.winterCourses.clear()
        for ((key, value) in generatedSchedule) {
            println("$key: ")
            print("\t")
            for (course in value) {
                print("${course.courseID}, ")
            }
            println("")
        }
//        val finalSchedule: MutableMap<String, MutableList<Course>> = finalizeSchedule(generatedSchedule, countCourseTerm, sequenceMap, prereqsData.toMutableMap())
        val finalSchedule = generatedSchedule
        for ((key, value) in finalSchedule) {
            println("$key: ")
            print("\t")
            for (course in value) {
                print("${course.courseID}, ")
            }
            println("")
        }
        return finalSchedule
    }

    private fun checkMathCourseConstraint(course: Course, termName : String, season: String,
                                          prereqMap: MutableMap<String, ParsedPrereqData>): Boolean {
        val parsedPrereqData = prereqMap[course.courseID] ?: return false
        var satisfyTerm = false
        if (course.availability!!.contains(season) && parsedPrereqData.minimumLevel <= termName) {
            satisfyTerm = true
        }

        if (parsedPrereqData.courses.isEmpty() || parsedPrereqData.courses.all {it.isEmpty()}) {
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

    private fun checkConstraint(course: Course, parsedDataMap:MutableMap<String, ParsedPrereqData>, retvalList: List<String>) : Boolean {
        val selectedCourseData = parsedDataMap[course.courseID]

        var satisfyPrereq = false
        var satisfyCoreq = false
        for (prereqCourseOption in selectedCourseData!!.courses) {
            var satisfy = true
            if (prereqCourseOption.size == 0) {
                continue
            }
            for (course in prereqCourseOption) {
                if (course !in takenCourses) {
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
                if (course !in takenCourses && course !in retvalList) {
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

    enum class NonMathAddStatus {
        AddToOnline,
        AddToInPerson,
        DontAdd,
    }
    private fun checkNonMathCourseConstraint(course: Course, termName : String, season: String,
                                             prereqMap: MutableMap<String, ParsedPrereqData>): NonMathAddStatus {
        val parsedPrereqData = prereqMap[course.courseID] ?: return NonMathAddStatus.DontAdd
        var satisfyTerm = false
        if (course.availability!!.contains(season) && parsedPrereqData.minimumLevel <= termName) {
            satisfyTerm = true
        }
        if (parsedPrereqData.courses.isEmpty() || parsedPrereqData.courses.all {it.isEmpty()}) {
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

    private fun generateCourseForTermSeason(
        termName: String,
        season: String,
        numCourse: Int,
        fallCourses: MutableSet<Course>,
        winterCourses: MutableSet<Course>,
        springCourses: MutableSet<Course>,
        parsedPrereqDataMap: MutableMap<String, ParsedPrereqData>
    ) : MutableList<Course> {
        var arrangeCourses = mutableSetOf<Course>()
        if (season == "F") {
            arrangeCourses = fallCourses
        } else if (season == "W") {
            arrangeCourses = winterCourses
        } else if (season == "S") {
            arrangeCourses = springCourses
        }
        var countAdded = 0
        val notTakenCourses = mutableListOf<Course>()
        val retvalList = mutableListOf<Course>()
        for (course in arrangeCourses) {
            if (course.courseID !in takenCourses) {
                notTakenCourses.add(course)
            }
        }
        if (termName == "1A" || termName == "1B") {
            for (course in notTakenCourses) {
                if (countAdded >= numCourse) {
                    break
                }
                if (course.courseID == "MATH 135" || course.courseID == "CS 135" || course.courseID == "MATH 145"
                    || course.courseID == "MATH 137" || course.courseID == "CS 115"
                    || course.courseID == "MATH 147" || course.courseID == "CS 145" || course.courseID in ListOneCourses) {
                    retvalList.add(course)
                    takenCourses.add(course.courseID)
                    countAdded++
                    continue
                }
                if (course.courseID == "MATH 136" || course.courseID == "MATH 146" || course.courseID == "MATH 138" ||
                    course.courseID == "MATH 148" || course.courseID == "CS 136" || course.courseID == "CS 116"
                    || course.courseID == "CS 146" || course.courseID == "STAT 230" || course.courseID == "STAT 240") {
                    retvalList.add(course)
                    takenCourses.add(course.courseID)
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
            val result = checkConstraint(course, parsedDataMap = parsedPrereqDataMap, retvalList=retvalList.map { it.courseID })
            if (result) {
                retvalList.add(course)
                countAdded++
            }
        }
        return retvalList
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
                // Add communication course for the first term
                if (termName == "1A") {
                    var communicationCourseAdded = false
                    for (item in newSatisfyConstraintNonMathCourse) {
                        val courseID = item.courseID
                        if (courseID in ListOneCourses) {
                            retvalList.add(item)
                            counter++
                            communicationCourseAdded = true
                            break
                        }
                    }
                    if (!communicationCourseAdded) {
                        for (item in newSatisfyConstraintOnlineNonMathCourse) {
                            val courseID = item.courseID
                            if (courseID in ListOneCourses) {
                                retvalList.add(item)
                                counter++
                                break
                            }
                        }
                    }
                }
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
        takenCourses.clear()
        return retvalList
    }

    private fun finalizeSchedule(scheduleSoFar: MutableMap<String, MutableList<Course>>,
                                 countCourseTerm: MutableMap<String, Int>, sequenceMap: Map<String, String>,
                                 prereqMap: MutableMap<String, ParsedPrereqData>): MutableMap<String, MutableList<Course>> {
        val availableCourses: MutableList<Course> = courseRepo.getAll().sortedWith(coursePlanner.courseComparator).toMutableList()
        for ((term, schedule) in scheduleSoFar) {
            // Remove taken courses so that no retake
            availableCourses.removeIf { course ->
                schedule.any { it.courseID == course.courseID }
            }
            var unfillCourseCount = countCourseTerm[term]!! - schedule.size

            if (unfillCourseCount <= 0) continue

            val addedCourses = mutableListOf<Course>()
            for (availableCourse in availableCourses) {
                val canAddCourse: Boolean = if (coursePlanner.mathSubjects.contains(availableCourse.subject)) {
                    checkMathCourseConstraint(availableCourse, term, sequenceMap[term]!!, prereqMap)
                } else {
                    checkNonMathCourseConstraint(availableCourse, term, sequenceMap[term]!!, prereqMap) != NonMathAddStatus.DontAdd
                }
                if (canAddCourse) {
                    scheduleSoFar[term]!!.add(availableCourse)
                    takenCourses.add(availableCourse.courseID)
                    unfillCourseCount--
                    addedCourses.add(availableCourse)
                    if (unfillCourseCount <= 0) break
                }
            }
            availableCourses.removeAll(addedCourses)
        }
        return scheduleSoFar
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

package repositories

import entities.Prerequisite
import entities.Course
import jakarta.inject.Inject
import org.hibernate.SessionFactory
class PrerequisiteRepo {
    @Inject
    private lateinit var sessionFactory: SessionFactory

    fun getParsedPrereqData(courseIds: List<String>): MutableMap<String, ParsedPrereqData> {
        return try {
            val map = mutableMapOf<String, ParsedPrereqData>()
            val session = sessionFactory.openSession()
            val hqlCourse = "FROM Course C WHERE C.courseID in :ids"
            val hql = "FROM Prerequisite P WHERE P.courseID in :ids"
            val prerequisites = session.createQuery(hql, Prerequisite::class.java)
                .setParameter("ids", courseIds)
            val coursesData = session.createQuery(hqlCourse, Course::class.java)
                .setParameter("ids", courseIds)
            val results = prerequisites.list()
            val resultsCourse = coursesData.list()
            results.forEach {
                val courses = if (it.courses != null && it.courses != "") {
                    it.courses!!.split(";").map { course ->
                        course.split(",").toMutableList()
                    }
                } else {
                    listOf()
                }
                val onlyOpenTo = if (it.onlyOpenTo != null && it.onlyOpenTo != "") {
                    it.onlyOpenTo!!.split(",").toMutableList()
                } else {
                    mutableListOf()
                }
                val notOpenTo = if (it.notOpenTo != null && it.notOpenTo != "") {
                    it.notOpenTo!!.split(",").toMutableList()
                } else {
                    mutableListOf()
                }
                val data = ParsedPrereqData(
                    courseID = it.courseID,
                    courses = courses.toMutableList(),
                    minimumLevel = it.minimumLevel ?: "",
                    onlyOpenTo = onlyOpenTo,
                    notOpenTo = notOpenTo,
                    antireqCourses = mutableListOf(),
                    coreqCourses = mutableListOf()
                )
                map[it.courseID] = data
            }
            resultsCourse.forEach {
                val coreqCourses = if (it.coreqs != null && it.coreqs != "") {
                    it.coreqs!!.split(";").map { coreqs ->
                        coreqs.split(",").toMutableList()
                    }
                } else {
                    mutableListOf()
                }
                val antireqCourses = if (it.antireqs != null && it.antireqs != "") {
                    it.antireqs!!.split(",").toMutableList()
                } else {
                    mutableListOf()
                }
                map[it.courseID]?.antireqCourses = antireqCourses
                map[it.courseID]?.coreqCourses = coreqCourses.toMutableList()
            }
            map
        } catch (e: Exception) {
            println(e)
            mutableMapOf()
        }
    }
}

data class ParsedPrereqData (
    val courseID: String = "",
    var courses: MutableList<MutableList<String>> = mutableListOf(),
    var coreqCourses: MutableList<MutableList<String>> = mutableListOf(),
    var antireqCourses: MutableList<String> = mutableListOf(),
    val minimumLevel: String = "",
    val onlyOpenTo: MutableList<String> = mutableListOf(),
    val notOpenTo: MutableList<String> = mutableListOf(),
)

package repositories

import entities.Prerequisite
import jakarta.inject.Inject
import org.hibernate.SessionFactory

class PrerequisiteRepo {
    @Inject
    private lateinit var sessionFactory: SessionFactory

    fun getParsedPrereqData(courseIds: List<String>): MutableMap<String, ParsedPrereqData> {
        return try {
            val map = mutableMapOf<String, ParsedPrereqData>()
            val session = sessionFactory.openSession()
            val hql = "FROM Prerequisite P WHERE P.courseID in :ids"
            val prerequisites = session.createQuery(hql, Prerequisite::class.java)
                .setParameter("ids", courseIds)
            val results = prerequisites.list()
            results.forEach {
                val courses = if (it.courses != null && it.courses != "") {
                    it.courses!!.split(";").map { course ->
                        course.split(",").toMutableList()
                    }
                } else {
                    listOf()
                }
                val data = ParsedPrereqData(
                    courseID = it.courseID,
                    courses = courses.toMutableList(),
                    minimumLevel = it.minimumLevel ?: ""
                )
                map[it.courseID] = data
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
    val courses: MutableList<MutableList<String>> = mutableListOf(),
    val minimumLevel: String = ""
)
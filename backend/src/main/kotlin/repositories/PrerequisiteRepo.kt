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
            val requirements = session.createQuery(hql, Prerequisite::class.java)
            requirements.setParameter("ids", courseIds)
            val results = requirements.list()
            for (requirement in results) {
                val courses = requirement.courses?.split(";")?.map {
                    it.split(",").toMutableList()
                }!!.toMutableList()
                map[requirement.courseID] = ParsedPrereqData(
                    coursesID = requirement.courseID,
                    courses = courses,
                    minimumLevel = requirement.minimumLevel!!
                )
            }
            map
        } catch (e: Exception) {
            println(e.message)
            mutableMapOf()
        }
    }
}

data class ParsedPrereqData (
    val coursesID: String = "",
    val courses: MutableList<MutableList<String>> = mutableListOf(),
    val minimumLevel: String = ""
)
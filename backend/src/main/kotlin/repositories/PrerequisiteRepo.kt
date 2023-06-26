package repositories

import entities.CourseID
import entities.Prerequisite
import jakarta.inject.Inject
import org.hibernate.SessionFactory

class PrerequisiteRepo {
    @Inject
    private lateinit var sessionFactory: SessionFactory

    fun getPrereqAndMinLvl(id: CourseID): Pair<String, String?>? {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Prerequisite WHERE courseID = :id"
            val query = session.createQuery(hql, Prerequisite::class.java)
            query.setParameter("id", id)
            val prerequisite = query.singleResult
            Pair(prerequisite.courseID, prerequisite.minimumLevel)
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }
}
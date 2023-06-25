package repositories

import entities.Minor
import entities.Requirement
import jakarta.inject.Inject
import org.hibernate.SessionFactory

class RequirementRepo {
    @Inject
    private lateinit var sessionFactory: SessionFactory

    fun getRequirementCoursesByIds(ids: Set<Long>): List<String> {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Requirement R WHERE R.requirementID in :ids"
            val requirements = session.createQuery(hql, Requirement::class.java)
            requirements.setParameter("ids", ids)
            requirements.list().mapNotNull { it.courses }
        } catch (e: Exception) {
            println(e.message)
            listOf()
        }
    }
}
package repositories

import entities.Major
import jakarta.inject.Inject
import org.hibernate.SessionFactory

class MajorRepo {
    @Inject
    private lateinit var sessionFactory: SessionFactory

    fun getRequirementIdByName(name: String): Long {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Major M WHERE M.majorName = :major_name"
            val majors = session.createQuery(hql, Major::class.java)
            majors.setParameter("major_name", name)
            majors.uniqueResult().requirementID
        } catch (e: Exception) {
            println(e.message)
            0
        }
    }
}
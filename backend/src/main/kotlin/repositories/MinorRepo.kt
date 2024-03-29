package repositories

import entities.Major
import entities.Minor
import jakarta.inject.Inject
import org.hibernate.SessionFactory

class MinorRepo {
    @Inject
    private lateinit var sessionFactory: SessionFactory

    fun getRequirementIdByName(name: String): Long {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Minor M WHERE M.minorName = :minor_name"
            val minors = session.createQuery(hql, Minor::class.java)
            minors.setParameter("minor_name", name)
            minors.list().last().requirementID
        } catch (e: Exception) {
            println(e.message)
            0
        }
    }

    fun getAllMinorNames(): Set<String> {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Minor"
            val majors = session.createQuery(hql, Minor::class.java)
            majors.list().map { it.minorName }.toSet()
        } catch (e: Exception) {
            println(e.message)
            setOf()
        }
    }
}
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
            majors.list().last().requirementID
        } catch (e: Exception) {
            println(e.message)
            0
        }
    }

    fun getAllMajorNames(): Set<String> {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Major"
            val majors = session.createQuery(hql, Major::class.java)
            majors.list().map { it.majorName }.toSet()
        } catch (e: Exception) {
            println(e.message)
            setOf()
        }
    }

    fun getMajorByName(majorName: String): Major? {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Major M WHERE M.majorName = :majorName"
            val query = session.createQuery(hql, Major::class.java)
            query.setParameter("majorName", majorName)
            query.resultList.firstOrNull()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }
}
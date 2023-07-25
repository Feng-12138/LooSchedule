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

    fun getAllMajorNames(): List<String> {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Major"
            val majors = session.createQuery(hql, Major::class.java)
            majors.list().map { it.majorName }
        } catch (e: Exception) {
            println(e.message)
            listOf()
        }
    }

    fun getMajorByName(majorName: String): Major? {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Major M WHERE M.majorName = :majorName"
            val query = session.createQuery(hql, Major::class.java)
            query.setParameter("majorName", majorName)
            query.uniqueResult()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    fun getMajorsByNames(majorNames: List<String>): List<Major> {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Major M WHERE M.majorName IN (:majorNames)"
            val query = session.createQuery(hql, Major::class.java)
            query.setParameterList("majorNames", majorNames)
            query.resultList
        } catch (e: Exception) {
            println(e.message)
            emptyList()
        }
    }
}
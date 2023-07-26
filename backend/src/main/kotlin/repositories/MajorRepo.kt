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
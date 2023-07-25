package repositories

import entities.Major
import entities.Minor
import entities.Specialization
import jakarta.inject.Inject
import org.hibernate.SessionFactory

class SpecializationRepo {
    @Inject
    private lateinit var sessionFactory: SessionFactory

    fun getRequirementIdByName(name: String): Long {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Specialization S WHERE S.specializationName = :specialization_name"
            val specializations = session.createQuery(hql, Specialization::class.java)
            specializations.setParameter("specialization_name", name)
            specializations.uniqueResult().requirementID
        } catch (e: Exception) {
            println(e.message)
            0
        }
    }

    fun getAllSpecializationsNames(): Set<String> {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Specialization"
            val majors = session.createQuery(hql, Specialization::class.java)
            majors.list().map { it.specializationName }.toSet()
        } catch (e: Exception) {
            println(e.message)
            setOf()
        }
    }
}
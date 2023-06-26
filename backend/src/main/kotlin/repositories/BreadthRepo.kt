package repositories

import entities.Breadth
import entities.Course
import jakarta.inject.Inject
import org.hibernate.SessionFactory

class BreadthRepo {
    @Inject
    private lateinit var sessionFactory: SessionFactory

    fun getAll(): List<Breadth> {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Breadth"
            val query = session.createQuery(hql, Breadth::class.java)
            val breadths = query.resultList
            breadths
        } catch (e: Exception) {
            println(e.message)
            emptyList<Breadth>()
        }
    }
}
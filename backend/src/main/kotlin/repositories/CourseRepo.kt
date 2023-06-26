package repositories

import entities.Course
import entities.CourseID
import jakarta.inject.Inject
import org.hibernate.SessionFactory

class CourseRepo {
    @Inject
    private lateinit var sessionFactory: SessionFactory

    fun getAll(): List<Course> {
        return try {
            val session = sessionFactory.openSession()
            val courses = session.createQuery("FROM Course", Course::class.java)
            courses.list()
        } catch (e: Exception) {
            println(e.message)
            listOf()
        }
    }

    fun getById(id: CourseID): Course? {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Course WHERE courseID = :id"
            val query = session.createQuery(hql, Course::class.java)
            query.setParameter("id", id)
            val course = query.uniqueResult()
            course
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }
}
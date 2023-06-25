package repositories

import entities.Course
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

    fun getById(): Course {
        TODO("Not yet implemented")
    }
}
package repositories

import entities.Communication
import entities.Course
import jakarta.inject.Inject
import org.hibernate.SessionFactory
import repositories.interfaces.IRepo

class CourseRepo : IRepo<Course> {
    @Inject
    private lateinit var sessionFactory: SessionFactory
    @Override
    override fun getAll(): List<Course> {
        return try {
            val session = sessionFactory.openSession()
            val courses = session.createQuery("FROM Course", Course::class.java)
            courses.list()
        } catch (e: Exception) {
            println(e.message)
            listOf()
        }
    }

    @Override
    override fun getById(): Course {
        TODO("Not yet implemented")
    }
}
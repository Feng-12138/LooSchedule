package repositories

import entities.Communication
import entities.Course
import jakarta.inject.Inject
import org.hibernate.SessionFactory
import repositories.interfaces.IRepo

class Repository : IRepo {
    @Inject
    private lateinit var sessionFactory: SessionFactory

    @Override
    override fun getCommunicationsAll(): List<Communication> {
        return try {
            val session = sessionFactory.openSession()
            val communications = session.createQuery("FROM Communication", Communication::class.java)
            communications.list()
        } catch (e: Exception) {
            println(e.message)
            listOf()
        }
    }

    @Override
    override fun getCoursesAll(): List<Course> {
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
    override fun helloWorld(): String {
        return "hello world"
    }
}
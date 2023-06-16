package repositories

import entities.Communication
import entities.Test
import entities.Course
import jakarta.inject.Inject
import org.hibernate.SessionFactory
import repositories.interfaces.ITestRepo

//@Singleton
//@Suppress("UNCHECKED_CAST")
class TestRepo: ITestRepo {
    @Inject
    private lateinit var sessionFactory: SessionFactory

    @Override
    override fun findAllNames(): List<String> {
        try {
            val session = sessionFactory.openSession()
            val tests = session.createQuery("FROM Test", Test::class.java)
//            val Course = session.createQuery("FROM Course", Course::class.java)
//            return Course.list().map { it.courseID }
            return tests.list().map { it.name }
        } catch (e: Exception) {
            println(e.message)
            return listOf()
        }
    }

    @Override
    override fun findCommunications(): List<Communication> {
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
    override fun helloWorld(): String {
        return "hello world"
    }

//    override fun findAllNames(): List<String> {
//        return session.createCriteria(Test::class.java)
//            .setProjection(
//                Projections.property("name")
//            )
//            .list() as List<String>
//    }
//
//    override fun save(test: Test) {
//        val transaction = session.beginTransaction()
//        session.save(test)
//        transaction.commit()
//    }
}
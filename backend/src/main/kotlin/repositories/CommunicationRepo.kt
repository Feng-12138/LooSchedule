package repositories

import entities.Communication
import jakarta.inject.Inject
import org.hibernate.SessionFactory

class CommunicationRepo {
    @Inject
    private lateinit var sessionFactory: SessionFactory

    fun getAll(): List<Communication> {
        return try {
            val session = sessionFactory.openSession()
            val communications = session.createQuery("FROM Communication", Communication::class.java)
            communications.list()
        } catch (e: Exception) {
            println(e.message)
            listOf()
        }
    }

    fun getById(): Communication {
        TODO("Not yet implemented")
    }
}
package repositories

import entities.Communication
import jakarta.inject.Inject
import org.hibernate.SessionFactory
import repositories.interfaces.IRepo

class CommunicationRepo: IRepo<Communication> {
    @Inject
    private lateinit var sessionFactory: SessionFactory
    @Override
    override fun getAll(): List<Communication> {
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
    override fun getById(): Communication {
        TODO("Not yet implemented")
    }
}
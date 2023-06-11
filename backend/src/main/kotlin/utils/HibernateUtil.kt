import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration

object HibernateUtil {
    val sessionFactory: SessionFactory = buildSessionFactory()
    private var session: Session? = null

    private fun buildSessionFactory(): SessionFactory {
        val configuration = Configuration().configure("hibernate.cfg.xml")
        return configuration.buildSessionFactory()
    }

    fun getSession(): Session {
        if (session == null || !session!!.isOpen) {
            session = sessionFactory.openSession()
        }
        return session!!
    }

    fun closeSession() {
        session?.close()
        session = null
    }
}

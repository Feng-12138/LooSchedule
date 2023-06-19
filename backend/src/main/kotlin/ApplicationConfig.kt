import controllers.Api
import jakarta.inject.Singleton
//import org.glassfish.jersey.guice.spi.container.GuiceComponentProviderFactory
import org.glassfish.jersey.internal.inject.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import repositories.CommunicationRepo
import repositories.CourseRepo
import repositories.TestRepo
import services.Service
import services.interfaces.IService


class ApplicationConfig : ResourceConfig() {
    init {
        register(Api::class.java)
        register(object : AbstractBinder() {
            override fun configure() {
                bind(TestRepo::class.java)
                    .to(TestRepo::class.java)
                    .`in`(Singleton::class.java)

                bind(CourseRepo::class.java)
                    .to(CourseRepo::class.java)
                    .`in`(Singleton::class.java)

                bind(CommunicationRepo::class.java)
                    .to(CommunicationRepo::class.java)
                    .`in`(Singleton::class.java)

                bind(Service::class.java)
                    .to(IService::class.java)
                    .`in`(Singleton::class.java)

                val sessionFactory = createSessionFactory()
                bind(sessionFactory)
                    .to(SessionFactory::class.java)
                    .`in`(Singleton::class.java)
            }

            private fun createSessionFactory(): SessionFactory {
                val configuration = Configuration().configure("hibernate.cfg.xml")
                return configuration.buildSessionFactory()
            }
        })
    }


}



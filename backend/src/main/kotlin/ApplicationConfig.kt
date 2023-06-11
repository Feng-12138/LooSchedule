import controllers.TestApi
import jakarta.inject.Singleton
import org.glassfish.jersey.internal.inject.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig
import repositories.TestRepo
import repositories.interfaces.ITestRepo
import services.TestService
import services.interfaces.ITestService


class ApplicationConfig : ResourceConfig() {
    init {
        register(TestApi::class.java)
//        register(TestService::class.java)

        register(object : AbstractBinder() {
            override fun configure() {
                bind(TestRepo::class.java)
                    .to(ITestRepo::class.java)
                    .`in`(Singleton::class.java)

                bind(TestService::class.java)
                    .to(ITestService::class.java)
                    .`in`(Singleton::class.java)

            }
        })
    }


}

//class SessionFactoryFactory(private val sessionFactory: SessionFactory) : Factory<Session> {
//    override fun provide(): Session {
//        return sessionFactory.openSession()
//    }
//
//    override fun dispose(instance: Session) {
//        instance.close()
//    }
//}



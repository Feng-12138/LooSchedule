import controllers.Api
import jakarta.inject.Singleton
//import org.glassfish.jersey.guice.spi.container.GuiceComponentProviderFactory
import org.glassfish.jersey.internal.inject.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import repositories.*
import services.*


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

                bind(MajorRepo::class.java)
                    .to(MajorRepo::class.java)
                    .`in`(Singleton::class.java)

                bind(MinorRepo::class.java)
                    .to(MinorRepo::class.java)
                    .`in`(Singleton::class.java)

                bind(SpecializationRepo::class.java)
                    .to(SpecializationRepo::class.java)
                    .`in`(Singleton::class.java)

                bind(RequirementRepo::class.java)
                    .to(RequirementRepo::class.java)
                    .`in`(Singleton::class.java)

                bind(Service::class.java)
                    .to(IService::class.java)
                    .`in`(Singleton::class.java)

                bind(RequirementsParser::class.java)
                    .to(RequirementsParser::class.java)
                    .`in`(Singleton::class.java)

                bind(TermMapperService::class.java)
                    .to(TermMapperService::class.java)
                    .`in`(Singleton::class.java)

                bind(CoursePlanner::class.java)
                    .to(CoursePlanner::class.java)
                    .`in`(Singleton::class.java)

                bind(SequenceGenerator::class.java)
                    .to(SequenceGenerator::class.java)
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



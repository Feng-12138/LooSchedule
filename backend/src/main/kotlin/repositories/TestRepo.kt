package repositories

import HibernateUtil
import entities.Test
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.hibernate.Session
import repositories.interfaces.ITestRepo
import org.hibernate.criterion.Projections

@Singleton
@Suppress("UNCHECKED_CAST")
class TestRepo: ITestRepo {

    override fun findById(id: Long): Test {
//        return Test()
        return HibernateUtil.getSession().get(Test::class.java, id)
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
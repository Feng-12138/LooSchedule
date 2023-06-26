package repositories

import entities.Communication
import entities.Course
import jakarta.inject.Inject
import org.hibernate.SessionFactory
import java.util.Calendar

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

    fun getList1ByYear(year: Int): MutableSet<Course> {
        val yearList = ArrayList<String>()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        for (y in year..currentYear) { yearList.add(y.toString() + "-" + (y + 1).toString()) }
        return try {
            val session = sessionFactory.openSession()
            val communicationHql = "Select Distinct courseID From Communication Where year in (:yearList) And listNumber = 1"
            val communicationQuery = session.createQuery(communicationHql, Communication::class.java)
            val courseIDs = communicationQuery.setParameterList("yearList", yearList).list()
            val courseHql = "From Course Where courseID in (:courseIDs)"
            val courseQuery = session.createQuery(courseHql, Course::class.java)
            val courses = courseQuery.setParameterList("courseIDs", courseIDs).list()
            courses.toMutableSet()
        } catch (e: Exception) {
            println(e.message)
            mutableSetOf<Course>()
        }
    }

    fun getList2ByYear(year: Int): MutableSet<Course> {
        val yearList = ArrayList<String>()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        for (y in year..currentYear) { yearList.add(y.toString() + "-" + (y + 1).toString()) }
        return try {
            val session = sessionFactory.openSession()
            val communicationHql = "Select Distinct courseID From Communication Where year in (:yearList) And listNumber = 2"
            val communicationQuery = session.createQuery(communicationHql, Communication::class.java)
            val courseIDs = communicationQuery.setParameterList("yearList", yearList).list()
            val courseHql = "From Course Where courseID in (:courseIDs)"
            val courseQuery = session.createQuery(courseHql, Course::class.java)
            val courses = courseQuery.setParameterList("courseIDs", courseIDs).list()
            courses.toMutableSet()
        } catch (e: Exception) {
            println(e.message)
            mutableSetOf<Course>()
        }
    }
}
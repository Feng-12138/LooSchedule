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

    fun getListNByYear(year: Int, listNumber: Int): MutableSet<Course> {
        val yearList = ArrayList<String>()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        for (y in year..currentYear) { yearList.add(y.toString() + "-" + (y + 1).toString()) }
        return try {
            val session = sessionFactory.openSession()
            val communicationHql = "From Communication c Where c.year in :yearList And c.listNumber = :listNumber"
            val communicationQuery = session.createQuery(communicationHql, Communication::class.java)
                .setParameter("yearList", yearList)
                .setParameter("listNumber", listNumber)
            val communicationCourses = communicationQuery.list()
            val courseIDs = communicationCourses.map { it.courseID }
            val courseHql = "From Course Where courseID in :courseIDs and availability != ''"
            val courseQuery = session.createQuery(courseHql, Course::class.java)
            val courses = courseQuery.setParameterList("courseIDs", courseIDs).list()
            courses.toMutableSet()
        } catch (e: Exception) {
            println(e.message)
            mutableSetOf()
        }
    }
}
package repositories

import entities.Course
import entities.CourseID
import services.utilities.Course as CourseData
import jakarta.inject.Inject
import org.hibernate.SessionFactory

class CourseRepo {
    @Inject
    private lateinit var sessionFactory: SessionFactory

    fun getAll(): List<Course> {
        return try {
            val session = sessionFactory.openSession()
            val courses = session.createQuery("FROM Course", Course::class.java)
            courses.list()
        } catch (e: Exception) {
            println(e.message)
            listOf()
        }
    }

    fun getAllIdAndNames(): List<String> {
        return try {
            val session = sessionFactory.openSession()
            val courses = session.createQuery("FROM Course", Course::class.java)
            courses.list().map { it.courseID +  " " + it.courseName}
        } catch (e: Exception) {
            println(e.message)
            listOf()
        }
    }

    fun getById(id: CourseID): Course? {
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Course WHERE courseID = :id"
            val query = session.createQuery(hql, Course::class.java)
            query.setParameter("id", id)
            val course = query.uniqueResult()
            course
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    fun getBySubjectCode(courseData: Set<CourseData>): MutableSet<Course> {
        val courseIDs = courseData.map { it.subject + " " + it.code  }
        return try {
            val session = sessionFactory.openSession()
            val hql = "FROM Course WHERE courseID in :ids"
            val query = session.createQuery(hql, Course::class.java)
            val courses = query.setParameterList("ids", courseIDs)
            courses.list().toMutableSet()
        } catch (e: Exception) {
            println(e.message)
            emptySet<Course>() as MutableSet<Course>
        }
    }

    fun getBySubject(courseSubjects: Set<String>, include: Boolean = true): MutableSet<Course> {
        return try {
            val session = sessionFactory.openSession()
            var hql = "FROM Course WHERE subject in :subjects and availability != '' and filledCount >= 30"
            if (!include) {
                hql = "FROM Course WHERE subject not in :subjects and availability != '' and filledCount >= 30"
            }
            val query = session.createQuery(hql, Course::class.java)
            val courses = query.setParameterList("subjects", courseSubjects)
            courses.list().toMutableSet()
        } catch (e: Exception) {
            println(e.message)
            emptySet<Course>() as MutableSet<Course>
        }
    }
}
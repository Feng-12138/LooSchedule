package services.interfaces

import entities.Communication
import entities.Course

//@Contract
interface IService {
    fun helloWorld(): String
    fun allCourses(): List<Course>
    fun allCommunications(): List<Communication>
}



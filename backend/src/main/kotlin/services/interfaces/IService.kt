package services.interfaces

import entities.Communication
import entities.Course

//import org.jvnet.hk2.annotations.Contract

//@Contract
interface IService {
    fun helloWorld(): String
    fun allCourses(): List<Course>
    fun allCommunications(): List<Communication>
}



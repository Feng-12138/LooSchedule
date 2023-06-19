package repositories.interfaces

import entities.Communication
import entities.Course

interface IRepo {
    fun getCoursesAll(): List<Course>
    fun getCommunicationsAll(): List<Communication>
    fun helloWorld(): String
}
package services

import entities.Communication
import entities.Course
import jakarta.inject.Inject
import repositories.Repository
import repositories.interfaces.IRepo
//import org.jvnet.hk2.annotations.Service
import services.interfaces.IService
class Service: IService {
    @Inject
    private lateinit var repository : IRepo

    @Override
    override fun helloWorld(): String {
        return repository.helloWorld()
    }
    @Override
    override fun allCourses(): List<Course> {
        return repository.getCoursesAll()
    }
    @Override
    override fun allCommunications(): List<Communication>{
        return repository.getCommunicationsAll()
    }
}
package services

import Schedule
import Scheduler
import entities.Communication
import entities.Course
import jakarta.inject.Inject
import repositories.CommunicationRepo
import repositories.CourseRepo
import repositories.TestRepo
//import org.jvnet.hk2.annotations.Service
import services.interfaces.IService
class Service: IService {
    @Inject
    private lateinit var courseRepo: CourseRepo
    @Inject
    private lateinit var communicationRepo: CommunicationRepo
    @Inject
    private lateinit var testRepo: TestRepo
    private lateinit var scheduler: Scheduler

    @Override
    override fun helloWorld(): String {
        return testRepo.helloWorld()
    }
    @Override
    override fun allCourses(): List<Course> {
        return courseRepo.getAll()
    }
    @Override
    override fun allCommunications(): List<Communication> {
        return communicationRepo.getAll()
    }

    @Override
    override fun generateSchedule(): Schedule {
        return scheduler.generateSchedule()
    }
}
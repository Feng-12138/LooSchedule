package services.interfaces

import Schedule
import com.google.common.util.concurrent.AbstractScheduledService.Scheduler
import entities.Communication
import entities.Course

//@Contract
interface IService {
    fun helloWorld(): String
    fun allCourses(): List<Course>
    fun allCommunications(): List<Communication>

    fun generateSchedule(): Schedule
}



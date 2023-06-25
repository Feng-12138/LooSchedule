package services

import entities.Communication
import entities.Course
import jakarta.inject.Inject
import repositories.*
//import org.jvnet.hk2.annotations.Service

class Service: IService {
    @Inject
    private lateinit var courseRepo: CourseRepo

    @Inject
    private lateinit var communicationRepo: CommunicationRepo

    @Inject
    private lateinit var majorRepo: MajorRepo

    @Inject
    private lateinit var minorRepo: MinorRepo

    @Inject
    private lateinit var specializationRepo: SpecializationRepo

    @Inject
    private lateinit var testRepo: TestRepo

    @Inject
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

    private fun getRequirements(plan: AcademicPlan) {
        val requirementsId = mutableSetOf<Long>()
        val requirementsData = mutableSetOf<String>()
        // get all requirement Ids
        for (major in plan.majors) {
            requirementsId.add(majorRepo.getRequirementIdByName(major))
        }
        for (minor in plan.minors) {
            requirementsId.add(minorRepo.getRequirementIdByName(minor))
        }
        for (specialization in plan.majors) {
            requirementsId.add(specializationRepo.getRequirementIdByName(specialization))
        }
    }
}


data class AcademicPlan(
    val majors: List<String> = listOf(),
    val startYear: String = "",
    val sequence: Int = 1,
    val minors: List<String> = listOf(),
    val specializations: List<String>,
)
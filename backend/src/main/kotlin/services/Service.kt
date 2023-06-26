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
    private lateinit var requirementRepo: RequirementRepo

    @Inject
    private lateinit var testRepo: TestRepo

    private lateinit var scheduler: Scheduler

    @Inject
    private lateinit var requirementsParser: RequirementsParser

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

    @Override
    override fun getRequirements(plan: AcademicPlan): Requirements {
        // get all requirement Ids
        try {
            val requirementsId = mutableSetOf<Long>()
            for (major in plan.majors) {
                requirementsId.add(majorRepo.getRequirementIdByName(major))
            }
            for (minor in plan.minors) {
                requirementsId.add(minorRepo.getRequirementIdByName(minor))
            }
            for (specialization in plan.majors) {
                requirementsId.add(specializationRepo.getRequirementIdByName(specialization))
            }

            val requirementsData = requirementRepo.getRequirementCoursesByIds(requirementsId)
            return requirementsParser.parseRequirements(requirementsData)
        } catch (e: Exception) {
            println(e)
            return Requirements()
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
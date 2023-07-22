package services

import entities.Communication
import entities.Course
import jakarta.inject.Inject
import repositories.*

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

    @Inject
    private lateinit var requirementsParser: RequirementsParser

    @Inject
    private lateinit var coursePlanner: CoursePlanner

    @Inject
    private lateinit var termMapperService: TermMapperService

    @Inject
    private lateinit var sequenceGenerator: SequenceGenerator

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
    override fun allPlanNames(): Plans {
        val majors = majorRepo.getAllMajorNames()
        val minors = minorRepo.getAllMinorNames()
        val specializations = specializationRepo.getAllSpecializationsNames()
        val courses = courseRepo.getAllIdAndNames()
        return Plans(
            majors = majors,
            minors = minors,
            specializations = specializations,
            courses = courses,
        )
    }

    @Override
    override fun generateSchedule(plan: AcademicPlan): MutableMap<String, MutableList<Course>> {
        val requirements: Requirements = getRequirements(plan)
        val selectedCourses = coursePlanner.getCoursesPlanToTake(plan.startYear, requirements)
        println(selectedCourses.first.map { it.courseID })
        println(selectedCourses.second.map { it.courseID })

        return termMapperService.mapCoursesToSequence(
            CourseDataClass(mathCourses = selectedCourses.first, nonMathCourses = selectedCourses.second),
            sequenceGenerator.generateSequence(plan.sequence)
        )
    }

    private fun getRequirements(plan: AcademicPlan): Requirements {
        // get all requirement Ids
        try {
            val requirementsId = mutableSetOf<Long>()
            for (major in plan.majors) {
                requirementsId.add(majorRepo.getRequirementIdByName(major))
            }
            for (minor in plan.minors) {
                requirementsId.add(minorRepo.getRequirementIdByName(minor))
            }
            for (specialization in plan.specializations) {
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

data class CourseSchedule(
    val term1ASchedule: Pair<String, List<Course>> = Pair("1A", listOf()),
    val term1BSchedule: Pair<String, List<Course>> = Pair("1B", listOf()),
    val term2ASchedule: Pair<String, List<Course>> = Pair("2A", listOf()),
    val term2BSchedule: Pair<String, List<Course>> = Pair("2B", listOf()),
    val term3ASchedule: Pair<String, List<Course>> = Pair("3A", listOf()),
    val term3BSchedule: Pair<String, List<Course>> = Pair("3B", listOf()),
    val term4ASchedule: Pair<String, List<Course>> = Pair("4A", listOf()),
    val term4BSchedule: Pair<String, List<Course>> = Pair("4B", listOf()),
    val term5ASchedule: Pair<String, List<Course>> = Pair("5A", listOf()),
    val coop1Schedule: Pair<String, List<Course>> = Pair("WT1", listOf()),
    val coop2Schedule: Pair<String, List<Course>> = Pair("WT2", listOf()),
    val coop3Schedule: Pair<String, List<Course>> = Pair("WT3", listOf()),
    val coop4Schedule: Pair<String, List<Course>> = Pair("WT4", listOf()),
    val coop5Schedule: Pair<String, List<Course>> = Pair("WT5", listOf()),
    val coop6Schedule: Pair<String, List<Course>> = Pair("WT6", listOf()),
)

data class AcademicPlan(
    var majors: List<String> = listOf(),
    var startYear: String = "",
    var sequence: String = "Regular",
    var minors: List<String> = listOf(),
    var specializations: List<String> = listOf()
) {
    // Default constructor
    constructor() : this(listOf(), "2023", "Regular", listOf(), listOf())
}
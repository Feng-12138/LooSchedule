package services

import entities.Communication
import entities.Course
import jakarta.inject.Inject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import repositories.*
import java.io.IOException
import java.util.concurrent.TimeUnit

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
    override suspend fun recommendCourses(position: String): List<services.Course> {
        val apiKey = System.getenv("API_KEY")
        val modelEndpoint = "https://api.openai.com/v1/chat/completions"
        val json = """
                        {
                          "model": "gpt-3.5-turbo",
                          "messages": [
                            {
                              "role": "user",
                              "content": "What courses should be taken to become an $position at University of Waterloo and what are the course number(only give me course number in answer with no other text, no intro and conclusion)"
                            }
                          ]
                        }
                        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(modelEndpoint)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        return fetchCourses(request)
    }

    private fun fetchCourses(request: Request): List<services.Course> {
        val client = OkHttpClient().newBuilder()
            .callTimeout(30, TimeUnit.SECONDS) // Set connection timeout
            .readTimeout(30, TimeUnit.SECONDS) // Set read timeout
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        val data = JSONObject(responseBody)
        val courses = data.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
            .replace("\n", ", ")
            .split(",")
            .map {
                val parts = it.trim().split(" ")
                println(parts)
                Course(parts[0], parts[1])
            }
            .toList()

        println(courses)

        return courses
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
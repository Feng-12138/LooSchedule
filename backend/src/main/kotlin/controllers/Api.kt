package controllers

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import kotlinx.coroutines.runBlocking
import okhttp3.*
import services.*

@Path("/")
class Api {
    @Inject
    private lateinit var service: IService

    @Inject
    private lateinit var scheduler: Scheduler

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun helloWorld(): Response {
        try {
            val message = service.helloWorld()
            return Response.ok(message).build()
        }
        catch (e: Exception){
//            println(e.message)
        }
        return Response.serverError().build()
    }

    @GET
    @Path("api/Communications")
    @Produces(MediaType.APPLICATION_JSON)
    fun getCommunications(): Response {
        try {
            val message = service.allCommunications()
            return Response.ok(message).build()
        } catch (e: Exception) {
            println(e.message)
        }
        return Response.serverError().build()
    }

    @POST
    @Path("api/schedule")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun getSchedule(academicPlan: AcademicPlan): Response {
        try {
            val message = scheduler.schedule(academicPlan)
            return Response.ok(message).build()
        } catch (e: Exception) {
            println(e.message)
        }
        return Response.serverError().build()
    }

    @GET
    @Path("api/everything")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllPlans(): Response {
        try {
            val message = service.allPlanNames()
            print(message)
            return Response.ok(message).build()
        } catch (e: Exception) {
            println(e.message)
        }
        return Response.serverError().build()
    }

    @POST
    @Path("api/recommendations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun tryGpt(request: RecommendationPlan): Response = runBlocking {
        try {
            val recommendedCourses = service.recommendCourses(request.position)
            println(recommendedCourses.map { it.subject + " " + it.code })
            val message = scheduler.schedule(request.academicPlan, recommendedCourses.toMutableList())
            return@runBlocking Response.ok(message).build()
        } catch (e: Exception) {
            println(e.message)
        }
        return@runBlocking Response.serverError().build()
    }

    @POST
    @Path("api/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun validateSchedule(scheduleToVerify: ScheduleValidator.ScheduleValidationInput): Response {
        try {
            val message = service.validateSchedule(scheduleToVerify)
            return Response.ok(message).build()
        } catch (e: Exception) {
            println(e.message)
        }
        return Response.serverError().build()
    }
}

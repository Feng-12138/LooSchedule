package controllers

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import kotlinx.coroutines.runBlocking
import okhttp3.*
import services.AcademicPlan
import services.IService

@Path("/")
class Api {
    @Inject
    private lateinit var service: IService

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
            val message = service.generateSchedule(academicPlan)
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
    @Path("gpt")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun tryGpt(request: Message): Response = runBlocking {
        try {
            println(request.position)
            val message = service.recommendCourses(request.position)
            return@runBlocking Response.ok(message).build()
        } catch (e: Exception) {
            println(e.message)
        }
        return@runBlocking Response.serverError().build()
    }

    data class Message(
        val position: String
    ){
        // Default constructor
        constructor() : this("")
    }
}

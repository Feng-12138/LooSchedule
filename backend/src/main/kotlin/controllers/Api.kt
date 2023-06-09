package controllers

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
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
    @Path("api/Courses")
    @Produces(MediaType.APPLICATION_JSON)
    fun getCourses(): Response {
        try{
            val message = service.allCourses()
            return Response.ok(message).build()
        }
        catch (e: Exception){
            println(e.message)
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
        }
        catch (e: Exception){
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
        }
        catch (e: Exception){
            println(e.message)
        }
        return Response.serverError().build()
    }
}

package controllers

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import services.IService

@Path("/")
class Api() {
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
            println(e.message)
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

    @GET
    @Path("api/GeneratedSchedule")
    @Produces(MediaType.APPLICATION_JSON)
    fun getSchedule(): Response {
        try {
            val message = service.generateSchedule()
            return Response.ok(message).build()
        }
        catch (e: Exception){
            println(e.message)
        }
        return Response.serverError().build()
    }
}

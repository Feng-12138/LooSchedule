package controllers

import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import repositories.TestRepo
import services.TestService
import services.interfaces.ITestService

@Path("/")
class TestApi() {
    @Inject
    private lateinit var testService: ITestService

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun helloWorld(): Response {
        val message = testService.helloWorld()
        return Response.ok(message).build()
    }

//    @GET
//    @Path("one")
//    @Produces(MediaType.APPLICATION_JSON)
//    fun one(): Response {
//        val message = testService.getTestById()
//        return Response.ok(message).build()
//    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAll(): Response {
        val message = testService.findAll()
        return Response.ok(message).build()
    }
}

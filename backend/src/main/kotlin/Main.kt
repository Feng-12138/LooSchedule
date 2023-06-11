import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import java.net.URI


fun main() {
    val config = ApplicationConfig()
    val server = GrizzlyHttpServerFactory.createHttpServer(
        URI.create("http://localhost:8080"),
        config,
        true,
    )
    println("Server running at http://localhost:8080")
    server.start()

}





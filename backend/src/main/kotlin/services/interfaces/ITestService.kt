package services.interfaces

import entities.Test

//import org.jvnet.hk2.annotations.Contract

//@Contract
interface   ITestService {
    fun helloWorld(): String

//    fun getTestById(): Test?

    fun findAll(): List<String>
}



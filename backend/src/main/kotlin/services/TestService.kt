package services

import entities.Communication
import jakarta.inject.Inject
//import org.jvnet.hk2.annotations.Service
import repositories.interfaces.ITestRepo
import services.interfaces.ITestService

//@Service
class TestService: ITestService {
    @Inject
    private lateinit var testRepo: ITestRepo

    @Override
    override fun helloWorld(): String {
        return testRepo.helloWorld()
    }

//    @Override
//    override fun getTestById(): Test? {
//        return testRepo.findById(1)
//    }
//
//
//
    @Override
    override fun findAll(): List<String> {
        return testRepo.findAllNames()
    }

    @Override
    override fun findCommunications(): List<Communication>{
        return testRepo.findCommunications()
    }
}
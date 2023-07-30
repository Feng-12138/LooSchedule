rootProject.name = "LooSchedule"
rootProject.buildFileName = "build.gradle.kts"

include (":backend")
project(":backend").projectDir = File("backend")
//project(":backend").buildFileName = "build.gradle.kts"

include (":AndroidApp")
project(":AndroidApp").projectDir = File("AndroidApp/app")
//project(":AndroidApp").buildFileName = "build.gradle"
//include("test")

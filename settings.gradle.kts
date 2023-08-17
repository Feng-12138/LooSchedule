rootProject.name = "looSchedule"
rootProject.buildFileName = "build.gradle.kts"

include "backend"
project(":backend").projectDir = new File("/backend")
project(":backend").buildFileName = "build.gradle.kts"

include "AndroidApp"
project(":AndroidApp").projectDir = new File("/AndroidApp")
project(":AndroidApp").buildFileName = "build.gradle.kts"
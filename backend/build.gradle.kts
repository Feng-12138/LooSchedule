plugins {
    kotlin("jvm") version "1.5.10"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:3.0.3")
    implementation("org.glassfish.jersey.inject:jersey-hk2:3.0.3")
    implementation("org.glassfish.jersey.core:jersey-server:3.0.3")
//    implementation("org.glassfish.jersey.inject:jersey-guice:3.0.3")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.0.3")
    implementation("org.hibernate:hibernate-core:5.4.33.Final")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("jakarta.activation:jakarta.activation-api:2.0.0")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")
    implementation("com.google.inject:guice:4.0")


//    val room_version = "2.5.1"
//
//    implementation("androidx.room:room-runtime:$room_version")
//    annotationProcessor("androidx.room:room-compiler:$room_version")
//
//    // To use Kotlin annotation processing tool (kapt)
//    implementation("androidx.room:room-compiler:$room_version")
//    // To use Kotlin Symbol Processing (KSP)
//    implementation("androidx.room:room-compiler:$room_version")

}

application {
    mainClass.set("MainKt")
}
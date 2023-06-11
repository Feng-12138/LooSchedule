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
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.0.3")
    implementation("org.hibernate:hibernate-core:5.4.33.Final")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("jakarta.activation:jakarta.activation-api:2.0.0")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")



}

application {
    mainClass.set("MainKt")
}
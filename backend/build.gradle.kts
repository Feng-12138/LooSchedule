plugins {
    kotlin("jvm") version "1.5.10"
    application
}

repositories {
    mavenCentral()
    maven("https://github.com/gwenn/sqlite-dialect/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    // Jersey
    implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:3.0.3")
    implementation("org.glassfish.jersey.inject:jersey-hk2:3.0.3")
    implementation("org.glassfish.jersey.core:jersey-server:3.0.3")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.0.3")

    // Hibernate & sqlite
    implementation("org.hibernate:hibernate-core:5.4.33.Final")
    implementation("org.xerial:sqlite-jdbc:3.34.0")
    implementation("com.github.gwenn:sqlite-dialect:0.1.1")

    // jaxb
    implementation("jakarta.activation:jakarta.activation-api:2.0.0")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.3")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")

    // guice
    implementation("com.google.inject:guice:4.0")

    // gson
    implementation("com.google.code.gson:gson:2.8.2")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.9.1")

}

application {
    mainClass.set("MainKt")
}
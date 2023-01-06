plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.mariadb.jdbc:mariadb-java-client:3.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
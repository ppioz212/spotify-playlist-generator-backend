plugins {
	java
	id("org.springframework.boot") version "3.1.4"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "com.asuresh"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
//	testImplementation("org.springframework.boot:spring-boot-starter-test")
//	implementation("com.fasterxml.jackson.core:jackson-core:2.16.0-rc1")
	implementation("org.springframework:spring-core:6.0.13")
	implementation("org.springframework:spring-jdbc:6.0.13")
	testImplementation(platform("org.junit:junit-bom:5.9.1"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	implementation("com.squareup.okhttp3:okhttp:4.11.0")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("org.json:json:20230618")
	implementation("org.postgresql:postgresql:42.6.0")
	implementation("org.apache.commons:commons-dbcp2:2.10.0")
    testImplementation("junit:junit:4.13.1")

}

tasks.withType<Test> {
	useJUnitPlatform()
}

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
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation(platform("org.junit:junit-bom:5.9.1"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	implementation("com.squareup.okhttp3:okhttp:4.11.0")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("org.json:json:20230618")
	implementation("org.postgresql:postgresql:42.6.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

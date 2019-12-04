import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.1.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.3.50"
	kotlin("plugin.spring") version "1.3.50"
	kotlin("plugin.jpa") version "1.3.50"
	kotlin("kapt") version "1.3.21"
}


group = "com.gram"
version = "0.1.g"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	compile("com.h2database:h2:1.4.199")
	compile("org.springframework.boot:spring-boot-starter-thymeleaf:2.2.0.RELEASE")
	compile("io.github.microutils:kotlin-logging:1.7.6")
	compile("mysql:mysql-connector-java:8.0.18")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	compile("org.jsoup:jsoup:1.10.3")
	compile("org.springframework.boot:spring-boot-starter-batch")
	compile("com.querydsl:querydsl-jpa:4.2.1")
	kapt("com.querydsl:querydsl-apt:4.2.1:jpa")
	compile("org.springframework.boot:spring-boot-starter-webflux:2.2.1.RELEASE")
	//	implementation("org.springframework.boot:spring-boot-starter-freemarker")

	compile("io.springfox:springfox-swagger2:2.9.2")
	compile("io.springfox:springfox-swagger-ui:2.9.2")


	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
	kotlin.srcDir("$buildDir/generated/source/kapt/main")
}


tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

plugins {
	id("fabric-loom") version "0.10-SNAPSHOT"
	`maven-publish`
}

val sourceCompatibility = JavaVersion.VERSION_17
val targetCompatibility = JavaVersion.VERSION_17

group = "net.fabricmc"
version = "1.0.0"

repositories {
	// Add repositories to retrieve artifacts from in here.
}

val minecraft_version: String = "1.18.1"
val yarn_mappings: String = "1.18.1+build.14"
val loader_version: String = "0.12.12"

val fabric_version: String = "0.45.0+1.18"

dependencies {
	minecraft("com.mojang:minecraft:${minecraft_version}")
	mappings("net.fabricmc:yarn:${yarn_mappings}:v2")

	modImplementation("net.fabricmc:fabric-loader:${loader_version}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
}

tasks {
	withType<JavaCompile> {
		options.encoding = "UTF-8"

		options.release.set(17)
	}

	processResources {
		inputs.property("version",  project.version)

		filesMatching("fabric.mod.json") {
			this.expand("version" to project.version)
		}
	}

	jar {
		from("LICENSE") {
			rename { "${it}_${rootProject.name}" }
		}
	}

	val sourcesJar by creating(Jar::class) {
		dependsOn(classes)
		archiveClassifier.convention("sources")
		from(sourceSets["main"].allSource)
	}

	publishing {
		publications {
			create<MavenPublication>("maven") {
				// add all the jars that should be included when publishing to maven
				artifact(remapJar.get())
				artifact(sourcesJar)
			}
		}

		// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
		repositories {
			// Add repositories to publish to here.
			// Notice: This block does NOT have the same function as the block in the top level.
			// The repositories here will be used for publishing your artifact, not for
			// retrieving dependencies.
		}
	}
}

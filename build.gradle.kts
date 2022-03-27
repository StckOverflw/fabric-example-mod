plugins {
	id("fabric-loom") version "0.11-SNAPSHOT"
	`maven-publish`
}

val sourceCompatibility = JavaVersion.VERSION_17
val targetCompatibility = JavaVersion.VERSION_17

group = "net.fabricmc"
version = "1.0.0"

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:1.18.2")
	mappings("net.fabricmc:yarn:1.18.2+build.2:v2")
	modImplementation("net.fabricmc:fabric-loader:0.13.3")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:0.48.0+1.18.2")

	// PSA: Some older mods, compiled on Loom 0.2.1, might have outdated Maven POMs.
	// You may need to force-disable transitiveness on them.
}

tasks {
	withType<JavaCompile> {
		// ensure that the encoding is set to UTF-8, no matter what the system default is
		// this fixes some edge cases with special characters not displaying correctly
		// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
		// If Javadoc is generated, this must be specified in that task too.
		options.encoding = "UTF-8"

		// The Minecraft launcher currently installs Java 8 for users, so your mod probably wants to target Java 8 too
		// JDK 9 introduced a new way of specifying this that will make sure no newer classes or methods are used.
		// We'll use that if it's available, but otherwise we'll use the older option.
		val targetVersion = 17
		if (JavaVersion.current().isJava9Compatible) {
			options.release.convention(targetVersion)
		}
	}

	processResources {
		inputs.property("version",  project.version)

		filesMatching("fabric.mod.json") {
			this.expand("version" to project.version)
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

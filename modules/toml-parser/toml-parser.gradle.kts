import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

val tomlc99Dir = file("$projectDir/src/nativeMain/c/tomlc99")

kotlin {
	sourceSets.getByName("commonMain") {
		dependencies {
			implementation(project(":io"))
		}
	}

	nativeTarget.compilations["main"].cinterops {
		create("tomlc99") {
			defFile("src/nativeMain/cinterop/tomlc99.def")
			includeDirs(tomlc99Dir.absolutePath)
		}
	}

	nativeTarget.binaries.all {
		linkerOpts("-L${tomlc99Dir.absolutePath}", "-l:libtoml.a")
	}

	explicitApi = ExplicitApiMode.Strict
}

tasks.create("buildTomlC99") {
	inputs.files(tomlc99Dir.listFiles { _, file ->
		file.endsWith(".c") || file.endsWith(".h")
	})

	outputs.file(tomlc99Dir.resolve("libtoml.a"))

	doLast {
		exec {
			workingDir = file(tomlc99Dir)
			commandLine("make", "libtoml.a")
		}
	}
}

tasks.named("cinteropTomlc99Native") {
	dependsOn("buildTomlC99")
}

plugins {
    id("dev.adamko.dokkatoo-html")
}

val ignoreEndings = sequenceOf("example" /* "-paper", "-velocity" */)

allprojects {
    if (ignoreEndings.any(name::endsWith)) {
        return@allprojects
    }
    apply(plugin = "dev.adamko.dokkatoo-html")

    tasks {
        val expandDocProperties = register<Copy>("expandMarkdownProperties") {
            from(layout.projectDirectory.dir("dokka/includes"))
            into(layout.buildDirectory.dir("dokka/includes.out"))
            val properties = mapOf(
                "project_group" to project.group,
                "project_name" to project.name,
                "project_version" to project.version,
                "dependency_notation" to "${project.group}:${project.name}:${project.version}"
            )
            inputs.properties(properties)
            expand(properties)
        }
        val concatenateMarkdown = register("concatenateMarkdown") {
            dependsOn(expandDocProperties)
            inputs.files(buildDir.resolve("dokka/includes.out").listFiles())
            outputs.file(buildDir.resolve("dokka/includes.out/page.md"))
            doLast {
                outputs.files.singleFile.writeText(inputs.files
                    .sortedBy { it.name }
                    .joinToString("\n\n") { it.readText() }
                )
            }
        }
        withType<dev.adamko.dokkatoo.tasks.DokkatooTask> {
            dependsOn(concatenateMarkdown)
        }
    }

    dokkatoo {
        moduleName.set(name)
        modulePath.set(name)
        dokkatooSourceSets.configureEach {
            includes.from(buildDir.resolve("dokka/includes.out/page.md"))
        }
        pluginsConfiguration.html {
            customAssets.from(rootProject.file("dokka/assets/logo-icon.svg"))
            footerMessage.set(
                "&copy; 2023 WarpedVoxels. voxels-core is licensed under the " +
                        "<a href=\"https://www.gnu.org/licenses/lgpl-3.0.en.html\">GNU Lesser General Public License v3.0</a>"
            )
        }
    }
}

dokkatoo {
    moduleName.set("voxels-core")
    modulePath.set("voxels-core")
    dokkatooPublications.configureEach {
        includes.from(buildDir.resolve("dokka/includes-out").listFiles())
    }
}

dependencies {
    subprojects.forEach {
        if (!ignoreEndings.any(it.name::endsWith)) dokkatoo(it)
    }
    dokkatooPluginHtml(
        dokkatoo.versions.jetbrainsDokka.map { dokkaVersion ->
            "org.jetbrains.dokka:all-modules-page-plugin:$dokkaVersion"
        }
    )
}

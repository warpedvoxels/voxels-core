plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

rootProject.name = "voxels-core"

includeBuild("gradle-plugin")
include(
    ":commands",
    ":commands:paper",
    ":commands:velocity",
    ":architecture",
    ":utility",
    ":networking",
    ":compose-ui",
    ":compose-ui:hud",
    ":compose-ui:maps",
    ":compose-ui:inventory",
    ":networking:paper",
    ":networking:velocity",
    ":resource-pack",
    ":behaviour-pack",
    ":example",
    ":proxy-core:architecture",
    ":proxy-core:utility",
    ":proxy-core:example"
)

fun recursiveApplySubprojectNames(project: ProjectDescriptor) {
    project.children.forEach { subproject ->
        if (subproject.name != "proxy-core") {
            subproject.name = project.name + "-" + subproject.name
        } else {
            subproject.name = "voxels-proxy-core"
        }
        recursiveApplySubprojectNames(subproject)
    }
}
recursiveApplySubprojectNames(rootProject)

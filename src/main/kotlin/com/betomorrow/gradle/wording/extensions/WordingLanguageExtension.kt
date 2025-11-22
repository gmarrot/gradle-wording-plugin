package com.betomorrow.gradle.wording.extensions

import com.betomorrow.gradle.wording.domain.OutputFormat
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import javax.inject.Inject

abstract class WordingLanguageExtension @Inject constructor(
    val name: String,
    private val projectLayout: ProjectLayout,
) {

    abstract val output: Property<String>

    abstract val column: Property<String>

    val isDefault: Boolean
        get() {
            return name == DEFAULT_NAME
        }

    fun getOutputFile(format: Provider<OutputFormat>): Provider<RegularFile> {
        return format.flatMap { outputFormat ->
            when (outputFormat) {
                OutputFormat.ANDROID -> getAndroidOutputFile()
                OutputFormat.SPRING -> getSpringOutputFile()
            }
        }
    }

    private fun getAndroidOutputFile(): Provider<RegularFile> {
        return output
            .map {
                val file = projectLayout.projectDirectory.file(it).asFile
                if (file.isDirectory) {
                    projectLayout.projectDirectory.file("$it/strings.xml")
                } else {
                    projectLayout.projectDirectory.file(it)
                }
            }
            .orElse(projectLayout.projectDirectory.file(getDefaultAndroidPath()))
    }

    private fun getDefaultAndroidPath(): String {
        return if (name == DEFAULT_NAME) {
            "src/main/res/values/strings.xml"
        } else {
            "src/main/res/values-$name/strings.xml"
        }
    }

    private fun getSpringOutputFile(): Provider<RegularFile> {
        return output
            .map {
                val file = projectLayout.projectDirectory.file(it).asFile
                if (file.isDirectory) {
                    projectLayout.projectDirectory.file("$it/messages.properties")
                } else {
                    projectLayout.projectDirectory.file(it)
                }
            }
            .orElse(projectLayout.projectDirectory.file(getDefaultSpringPath()))
    }

    private fun getDefaultSpringPath(): String {
        return if (name == DEFAULT_NAME) {
            "src/main/resources/messages.properties"
        } else {
            "src/main/resources/messages_$name.properties"
        }
    }

    companion object {
        const val DEFAULT_NAME = "default"
    }
}

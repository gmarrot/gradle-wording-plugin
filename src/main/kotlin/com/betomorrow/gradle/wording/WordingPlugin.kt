package com.betomorrow.gradle.wording

import com.betomorrow.gradle.wording.extensions.WORDING_EXTENSION_NAME
import com.betomorrow.gradle.wording.extensions.WordingLanguageExtension
import com.betomorrow.gradle.wording.extensions.WordingPluginExtension
import com.betomorrow.gradle.wording.tasks.DownloadWordingTask
import com.betomorrow.gradle.wording.tasks.UpdateWordingTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class WordingPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val wordingExtension = project.extensions.create(WORDING_EXTENSION_NAME, WordingPluginExtension::class.java)

        val downloadWordingTask = project.tasks.register("downloadWording", DownloadWordingTask::class.java) { t ->
            t.group = GROUP
            t.description = "Download translations from Google Sheets"

            t.credentials.set(
                wordingExtension.credentials.map { project.layout.projectDirectory.file(it) }
            )
            t.clientId.set(wordingExtension.clientId)
            t.clientSecret.set(wordingExtension.clientSecret)

            t.fileId.set(wordingExtension.sheetId)
            t.output.set(wordingExtension.wordingFile)

            t.outputs.upToDateWhen { false }
        }

        val updateWordingTask = project.tasks.register("updateWording") { t ->
            t.group = GROUP
            t.description = "Update all wording files"

            t.mustRunAfter(downloadWordingTask)
        }

        wordingExtension.languages.all { language ->
            val outputFile = language.getOutputFile(wordingExtension.outputFormat)
            val task = project.tasks.register(
                "updateWording${language.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}",
                UpdateWordingTask::class.java
            ) { t ->
                t.group = GROUP
                t.description = "Update wording file for language: ${language.name}"

                t.skipHeaders.set(wordingExtension.skipHeaders)
                t.source.set(wordingExtension.wordingFile)
                t.output.set(outputFile)
                t.outputFormat.set(wordingExtension.outputFormat)
                t.keysColumn.set(wordingExtension.keysColumn)
                t.column.set(language.column)
                t.sheetNames.set(wordingExtension.sheetNames)
                t.failOnMissingKeys.set(language.name == WordingLanguageExtension.DEFAULT_NAME)
                t.addMissingKeys.set(wordingExtension.addMissingKeys)

                t.mustRunAfter(downloadWordingTask)
            }
            updateWordingTask.configure { it.dependsOn(task) }
        }

        project.tasks.register("upgradeWording") { t ->
            t.group = GROUP
            t.description = "Download and update all wording files"
            t.dependsOn(downloadWordingTask, updateWordingTask)
        }
    }

    companion object {
        const val GROUP = "Wording"
    }
}

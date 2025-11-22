package com.betomorrow.gradle.wording.tasks

import com.betomorrow.gradle.wording.infra.drive.DriveMimeType
import com.betomorrow.gradle.wording.infra.drive.GoogleDrive
import org.gradle.api.DefaultTask
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class DownloadWordingTask : DefaultTask() {

    @get:Optional
    @get:Input
    abstract val clientId: Property<String>

    @get:Optional
    @get:Input
    abstract val clientSecret: Property<String>

    @get:Optional
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val credentials: RegularFileProperty

    @get:Input
    abstract val fileId: Property<String>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @get:Inject
    abstract val layout: ProjectLayout

    @TaskAction
    fun download() {
        val googleDrive = when {
            clientId.isPresent && clientSecret.isPresent ->
                GoogleDrive(clientId.get(), clientSecret.get(), tokenDirectory)

            credentials.isPresent -> GoogleDrive(credentials.get().asFile, tokenDirectory)
            else -> GoogleDrive(tokenDirectory)
        }

        logger.info("download $fileId to $output")
        googleDrive.downloadFile(fileId.get(), DriveMimeType.XLSX, output.get().asFile)
    }

    private val tokenDirectory: String
        get() {
            return layout.projectDirectory
                .dir(".gradle/wording-plugin/tokens")
                .asFile
                .absolutePath
        }
}

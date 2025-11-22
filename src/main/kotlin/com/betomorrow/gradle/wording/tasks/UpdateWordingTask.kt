package com.betomorrow.gradle.wording.tasks

import com.betomorrow.gradle.wording.domain.OutputFormat
import com.betomorrow.gradle.wording.domain.updater.WordingUpdaterFactory
import com.betomorrow.gradle.wording.domain.xlsx.Column
import com.betomorrow.gradle.wording.domain.xlsx.XlsxExtractor
import org.gradle.api.DefaultTask
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Paths
import javax.inject.Inject

abstract class UpdateWordingTask : DefaultTask() {

    @get:InputFile
    abstract val source: RegularFileProperty

    @get:OutputFile
    abstract val output: RegularFileProperty

    @get:Input
    abstract val skipHeaders: Property<Boolean>

    @get:Input
    abstract val keysColumn: Property<String>

    @get:Input
    abstract val column: Property<String>

    @get:Input
    abstract val sheetNames: ListProperty<String>

    @get:Input
    abstract val failOnMissingKeys: Property<Boolean>

    @get:Input
    abstract val addMissingKeys: Property<Boolean>

    @get:Input
    abstract val outputFormat: Property<OutputFormat>

    @get:Inject
    abstract val layout: ProjectLayout

    @TaskAction
    fun update() {
        val sourceFile = source.get().asFile
        val outputFile = output.get().asFile

        val extractor = XlsxExtractor(sourceFile.absolutePath, Column(keysColumn.get()), skipHeaders.get())
        val updater = WordingUpdaterFactory().build(outputFormat.get(), Paths.get(outputFile.absolutePath))

        val wordings = extractor.extract(Column(column.get()), sheetNames.get())
        val updatedKeys = updater.update(wordings, addMissingKeys.get())

        val missingKeys = wordings.keys - updatedKeys
        if (missingKeys.isNotEmpty() && failOnMissingKeys.get()) {
            throw MissingKeyException(missingKeys, outputFile.relativeTo(layout.projectDirectory.asFile))
        }
    }
}

class MissingKeyException(keys: Set<String>, file: File) :
    Exception("Missing Keys [${keys.joinToString(", ")}] in file $file")

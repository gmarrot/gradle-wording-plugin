package com.betomorrow.gradle.wording.extensions

import com.betomorrow.gradle.wording.domain.OutputFormat
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import javax.inject.Inject

const val WORDING_EXTENSION_NAME = "wording"

abstract class WordingPluginExtension @Inject constructor(
    objects: ObjectFactory,
    projectLayout: ProjectLayout,
) {
    abstract val credentials: Property<String>
    abstract val clientId: Property<String>
    abstract val clientSecret: Property<String>

    abstract val sheetId: Property<String>
    abstract val sheetNames: ListProperty<String>

    abstract val skipHeaders: Property<Boolean>

    abstract val filename: Property<String>

    abstract val keysColumn: Property<String>

    abstract val addMissingKeys: Property<Boolean>

    abstract val outputFormat: Property<OutputFormat>

    val wordingFile: Provider<RegularFile> =
        filename.map { projectLayout.projectDirectory.file(it) }

    val languages: NamedDomainObjectContainer<WordingLanguageExtension> =
        objects.domainObjectContainer(WordingLanguageExtension::class.java)

    init {
        sheetNames.empty()
        skipHeaders.convention(true)
        filename.convention("wording.xlsx")
        keysColumn.convention("A")
        addMissingKeys.convention(false)
        outputFormat.convention(OutputFormat.ANDROID)
    }
}

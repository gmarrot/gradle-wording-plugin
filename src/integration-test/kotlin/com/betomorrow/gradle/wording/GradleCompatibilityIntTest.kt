package com.betomorrow.gradle.wording

import org.assertj.core.api.Assertions.assertThat
import org.gradle.internal.impldep.org.junit.Rule
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class GradleCompatibilityIntTest {
    @Rule
    private val projectDir = TemporaryFolder()

    abstract inner class CompatibilityTest {
        @BeforeEach
        fun setup() {
            projectDir.create()

            val wordingFile = projectDir.newFile("wording.xlsx")
            this::class.java.getResourceAsStream("/wording.xlsx").use { input ->
                wordingFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            prepareProject()
        }

        abstract fun prepareProject()
    }

    @Nested
    inner class GroovyDsl : CompatibilityTest() {
        override fun prepareProject() {
            val buildFile = projectDir.newFile("build.gradle")
            buildFile.appendText(
                """
                import com.betomorrow.gradle.wording.domain.OutputFormat

                plugins {
                    id 'com.betomorrow.gradle.wording'
                }

                wording {
                    credentials = "~/.credentials.json"
                    clientId = ""
                    clientSecret = ""

                    sheetId = "qwertyuiop"
                    sheetNames = ["Feuille 1"]


                    filename = "wording.xlsx"
                    skipHeaders = true
                    keysColumn = "A"

                    addMissingKeys = true

                    outputFormat = OutputFormat.SPRING

                    languages {
                        'default' {
                            column = "C"
                        }
                        'fr' {
                            column = "D"
                        }
                    }
                }
                """.trimIndent()
            )
        }

        @ParameterizedTest
        @MethodSource("com.betomorrow.gradle.wording.GradleCompatibilityIntTest#compatibleGradleVersions")
        fun `plugin should be compatible with Gradle version`(gradleVersion: String) {
            val result = GradleRunner.create()
                .withProjectDir(projectDir.root)
                .withArguments("updateWording", "--stacktrace")
                .withPluginClasspath()
                .withDebug(true)
                .withGradleVersion(gradleVersion)
                .build()

            println(result.output)

            assertThat(result.task(":updateWording")?.outcome)
                .isEqualTo(TaskOutcome.SUCCESS)
            assertThat(result.task(":updateWordingDefault")?.outcome)
                .isEqualTo(TaskOutcome.SUCCESS)
            assertThat(result.task(":updateWordingFr")?.outcome)
                .isEqualTo(TaskOutcome.SUCCESS)

            assertThat(projectDir.root.resolve("src/main/resources/messages.properties").readText())
                .contains("key1=en value 1")
            assertThat(projectDir.root.resolve("src/main/resources/messages_fr.properties").readText())
                .contains("key1=fr value 1")
        }
    }

    @Nested
    inner class KotlinDslLegacy : CompatibilityTest() {
        override fun prepareProject() {
            val buildFile = projectDir.newFile("build.gradle.kts")
            buildFile.appendText(
                """
                import com.betomorrow.gradle.wording.domain.OutputFormat

                plugins {
                    id("com.betomorrow.gradle.wording")
                }

                wording {
                    credentials.set("~/.credentials.json")
                    clientId.set("")
                    clientSecret.set("")

                    sheetId.set("qwertyuiop")
                    sheetNames.add("Feuille 1")


                    filename.set("wording.xlsx")
                    skipHeaders.set(true)
                    keysColumn.set("A")

                    addMissingKeys.set(true)

                    outputFormat.set(OutputFormat.SPRING)

                    languages {
                        register("default") {
                            column.set("C")
                        }
                        register("fr") {
                            column.set("D")
                        }
                    }
                }
                """.trimIndent()
            )
        }

        @ParameterizedTest
        @MethodSource("com.betomorrow.gradle.wording.GradleCompatibilityIntTest#compatibleGradleVersions")
        fun `plugin should be compatible with Gradle version`(gradleVersion: String) {
            val result = GradleRunner.create()
                .withProjectDir(projectDir.root)
                .withArguments("updateWording", "--stacktrace")
                .withPluginClasspath()
                .withDebug(true)
                .withGradleVersion(gradleVersion)
                .build()

            println(result.output)

            assertThat(result.task(":updateWording")?.outcome)
                .isEqualTo(TaskOutcome.SUCCESS)
            assertThat(result.task(":updateWordingDefault")?.outcome)
                .isEqualTo(TaskOutcome.SUCCESS)
            assertThat(result.task(":updateWordingFr")?.outcome)
                .isEqualTo(TaskOutcome.SUCCESS)

            assertThat(projectDir.root.resolve("src/main/resources/messages.properties").readText())
                .contains("key1=en value 1")
            assertThat(projectDir.root.resolve("src/main/resources/messages_fr.properties").readText())
                .contains("key1=fr value 1")
        }
    }

    @Nested
    inner class KotlinDsl : CompatibilityTest() {
        override fun prepareProject() {
            val buildFile = projectDir.newFile("build.gradle.kts")
            buildFile.appendText(
                """
                import com.betomorrow.gradle.wording.domain.OutputFormat

                plugins {
                    id("com.betomorrow.gradle.wording")
                }

                wording {
                    credentials = "~/.credentials.json"
                    clientId = ""
                    clientSecret = ""

                    sheetId = "qwertyuiop"
                    sheetNames.addAll("Feuille 1")


                    filename = "wording.xlsx"
                    skipHeaders = true
                    keysColumn = "A"

                    addMissingKeys = true

                    outputFormat = OutputFormat.SPRING

                    languages {
                        register("default") {
                            column = "C"
                        }
                        register("fr") {
                            column = "D"
                        }
                    }
                }
                """.trimIndent()
            )
        }

        @ParameterizedTest
        @MethodSource("com.betomorrow.gradle.wording.GradleCompatibilityIntTest#compatibleKotlinDslGradleVersions")
        fun `plugin should be compatible with Gradle version`(gradleVersion: String) {
            val result = GradleRunner.create()
                .withProjectDir(projectDir.root)
                .withArguments("updateWording", "--stacktrace")
                .withPluginClasspath()
                .withDebug(true)
                .withGradleVersion(gradleVersion)
                .build()

            println(result.output)

            assertThat(result.task(":updateWording")?.outcome)
                .isEqualTo(TaskOutcome.SUCCESS)
            assertThat(result.task(":updateWordingDefault")?.outcome)
                .isEqualTo(TaskOutcome.SUCCESS)
            assertThat(result.task(":updateWordingFr")?.outcome)
                .isEqualTo(TaskOutcome.SUCCESS)

            assertThat(projectDir.root.resolve("src/main/resources/messages.properties").readText())
                .contains("key1=en value 1")
            assertThat(projectDir.root.resolve("src/main/resources/messages_fr.properties").readText())
                .contains("key1=fr value 1")
        }
    }

    companion object {
        @JvmStatic
        fun compatibleGradleVersions(): Stream<String> = Stream.of(
            "7.0",
            "7.2",
            "7.6.6",
            "8.0.2",
            "8.14.3",
        )

        @JvmStatic
        fun compatibleKotlinDslGradleVersions(): Stream<String> = Stream.of(
            "8.2",
            "8.5",
            "8.14.3",
        )
    }
}

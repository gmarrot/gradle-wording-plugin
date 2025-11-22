package com.betomorrow.gradle.wording

import org.assertj.core.api.Assertions.assertThat
import org.gradle.internal.impldep.org.junit.Rule
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import java.io.File

class WordingPluginIntTest {

    @Rule
    val testProjectDir = TemporaryFolder()

    @Test
    fun `test update wording on Android config`() {
        val result = GradleRunner.create()
            .withProjectDir(File("src/integration-test/resources/sample-android"))
            .withArguments(
                "updateWordingFr",
                "--stacktrace"
            )
            .withPluginClasspath()
            .withDebug(true)
            .build()

        println(result.output)

        assertThat(result.task(":updateWordingFr")?.outcome)
            .isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `test update wording on Spring config with Groovy DSL`() {
        val result = GradleRunner.create()
            .withProjectDir(File("src/integration-test/resources/sample-spring"))
            .withArguments(
                "updateWordingFr",
                "--stacktrace"
            )
            .withPluginClasspath()
            .withDebug(true)
            .build()

        println(result.output)

        assertThat(result.task(":updateWordingFr")?.outcome)
            .isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `test update wording on Spring config with Kotlin DSL`() {
        val result = GradleRunner.create()
            .withProjectDir(File("src/integration-test/resources/sample-spring-kotlin-dsl"))
            .withArguments(
                "updateWordingFr",
                "--stacktrace"
            )
            .withPluginClasspath()
            .withDebug(true)
            .build()

        println(result.output)

        assertThat(result.task(":updateWordingFr")?.outcome)
            .isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `test apply plugin with Groovy DSL`() {
        testProjectDir.create()
        val buildFile = testProjectDir.newFile("build.gradle")

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
                sheetNames = ["commons", "app"]
                filename = "wording.xlsx"
                skipHeaders = true
                keysColumn = "A"
                outputFormat = OutputFormat.SPRING

                languages {
                    'default' {
                        column = "C"
                    }
                    'fr' {
                        column = "D"
                    }
                    'es' {
                        column = "E"
                    }
                }
            }
            """.trimIndent()
        )

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("tasks", "--stacktrace", "--all")
            .withPluginClasspath()
            .withDebug(true)
            .build()

        println(result.output)

        assertThat(result.task(":tasks")?.outcome)
            .isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    fun `test apply plugin with Kotlin Dsl`() {
        testProjectDir.create()
        val buildFile = testProjectDir.newFile("build.gradle.kts")

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
                sheetNames.addAll("commons", "app")
                filename.set("wording.xlsx")
                skipHeaders.set(true)
                keysColumn.set("A")
                outputFormat.set(OutputFormat.SPRING)

                languages {
                    register("default") {
                        column.set("C")
                    }
                    register("fr") {
                        column.set("D")
                    }
                    register("es") {
                        column.set("E")
                    }
                }
            }
            """.trimIndent()
        )

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("tasks", "--stacktrace", "--all")
            .withPluginClasspath()
            .withDebug(true)
            .build()

        println(result.output)

        assertThat(result.task(":tasks")?.outcome)
            .isEqualTo(TaskOutcome.SUCCESS)
    }
}

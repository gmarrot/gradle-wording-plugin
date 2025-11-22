import com.betomorrow.gradle.wording.domain.OutputFormat

plugins {
    id("com.betomorrow.gradle.wording")
}

wording {
    sheetId.set("1UznpBPuRddr5gYPnRhNxkP-bcU0NHkhpTqi6B5V0QL0")
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

# Android and Spring Messages Wording Gradle Plugin

## Summary

This plugin allows you to manage Android or Spring app's wording with a simple Google Sheet file.
Just create a sheet with columns for keys and wording.
The plugin will generate or update existing strings files.
Your product owner will be able to edit himself wording for Spring or Android applications.

## Requirements

This plugin requires Gradle 7.0 or higher to work as it uses APIs that are not available in lower versions.

## Quick Start

You can find a sample sheet [here](https://docs.google.com/spreadsheets/d/1UznpBPuRddr5gYPnRhNxkP-bcU0NHkhpTqi6B5V0QL0/edit?usp=sharing) but it's just a simple sheet with one column for keys and columns for languages like this

| Keys           | English   | French |
|----------------|-----------|--------|
| user_firstname | Firstname | Prénom |
| user_lastname  | Lastname  | Nom    |

### Plugin Configuration

#### Groovy DSL

First, you need to apply the plugin in your `build.gradle`.

```groovy
plugins {
    id "com.betomorrow.gradle.wording" version "3.1.0"
}
```

Or you can use the legacy plugin declaration.

```groovy
buildscript {
    repositories {
        maven {
            url 'https://plugins.gradle.org/m2/'
        }
    }
    dependencies {
        classpath "com.betomorrow.gradle-plugins:gradle-wording-plugin:3.1.0"
    }
}

apply plugin: "com.betomorrow.gradle.wording"
```

Then, you can configure the plugin in your `build.gradle`.

```groovy
wording {
    sheetId = "1CLtBMPTXJC2SAzdXMK1uIRc7tkVRUTSnGC28RBmzgeU"
    languages {
        'default' {
            column = "C"
        }
        'fr' {
            column = "D"
        }
    }
}
```

#### Kotlin DSL

First, you need to apply the plugin in your `build.gradle.kts`.

```kotlin
plugins {
    id("com.betomorrow.gradle.wording") version "3.1.0"
}
```

Or you can use the legacy plugin declaration.

```kotlin
buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("com.betomorrow.gradle-plugins:gradle-wording-plugin:3.1.0")
    }
}

apply(plugin = "com.betomorrow.gradle.wording")
```

Then, you can configure the plugin in your `build.gradle.kts`.

__Gradle >= 8.2:__

```kotlin
wording {
    sheetId = "1CLtBMPTXJC2SAzdXMK1uIRc7tkVRUTSnGC28RBmzgeU"
    languages {
        register("default") {
            column = "C"
        }
        register("fr") {
            column = "D"
        }
    }
}
```

__Gradle < 8.2:__

```kotlin
wording {
    sheetId.set("1CLtBMPTXJC2SAzdXMK1uIRc7tkVRUTSnGC28RBmzgeU")
    languages {
        register("default") {
            column.set("C")
        }
        register("fr") {
            column.set("D")
        }
    }
}
```

### Usage

Run the command `./gradlew :app:upgradeWording`.

It will ask you to grant access on Google Sheet.

```bash
> Task :app:downloadWording
Please open the following address in your browser:
  https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id=971125274965-0glt9eqo63417es0nbhkmb6rj2i31g2p.apps.googleusercontent.com&redirect_uri=http://localhost:8888/Callback&response_type=code&scope=https://www.googleapis.com/auth/drive
```

Copy / Paste url in your browser, accept authorization and close browser

[Authorization Sample](https://github.com/oliviergauthier/gradle-wording-plugin/blob/master/images/authorization.png)

It will update wording files:  

- for Android output format, the generated files will be `app/src/main/res/values/strings.xml` and `app/src/main/res/values-fr/strings.xml`
- for Spring output format, they will be `src/main/resources/messages.properties` and `src/main/resources/messages_fr.properties`.

## Tasks

Plugin creates several tasks to manage wording:

- **downloadWording**: Export Google Sheet in a local XLSX file that you can commit for later edit. It prevents risks of having unwanted wording changes when you fix bugs.
- **updateWording**: Update all wording files using the local XLSX file.
- **upgradeWording**: Download the Google Sheet and update all wording files.

It also creates tasks for each defined language: **updateWordingDefault**, **updateWordingFr**, ...

## Complete DSL

### Groovy DSL

```groovy
wording {
    credentials = "credentials.json"    // Optional. Default: use provided credentials  
    clientId = ""                       // Optional. Default: use provided credentials  
    clientSecret = ""                   // Optional. Default: use provided credentials  

    sheetId = "THE SHEET ID"            // *Required*
    sheetNames = ["commons", "app"]     // Optional. Default: use all sheetName
    filename = "wording.xlsx"           // Optional. Default: "wording.xlsx"
    keysColumn = "A"                    // Optional. Default: "A"

    skipHeaders = true                  // Optional. Skip header line of each sheet (= first line of the sheet). Default: true
    addMissingKeys = true               // Optional. Add missing keys from sheet in wording files. If false, it will throw errors on default wording file when some keys are missing. Default true
    outputFormat = OutputFormat.ANDROID // Optional. The output format for wording files. Possible values are OutputFormat.ANDROID and OutputFormat.SPRING. Default: OutputFormat.ANDROID

    languages {
        'default' {
            output=  "src/main/resources/messages.properties" // Optional. Default: "src/main/res/values/strings.xml" for Android, "src/main/resources/messages.properties" for Spring.
            column = "B"                                      // *Required* The column containing the wording for the language.
        }
        'fr' {
            output = "src/main/resources/messages_france.properties" // Optional, Default: "src/main/res/values-<LANGUAGE>/strings.xml" for Android, "src/main/resources/messages_<LANGUAGE>.properties" for Spring.
            column = "C"                                             // *Required* The column containing the wording for the language.
        }
        // [...] Add more languages here
    }
}
```

### Kotlin DSL

__Gradle >= 8.2:__

```kotlin
wording {
    credentials = "credentials.json"    // Optional. Default: use provided credentials  
    clientId = ""                       // Optional. Default: use provided credentials  
    clientSecret = ""                   // Optional. Default: use provided credentials  

    sheetId = "THE SHEET ID"            // *Required*
    sheetNames.addAll("commons", "app") // Optional. Default: use all sheetName
    filename = "wording.xlsx"           // Optional. Default: "wording.xlsx"
    keysColumn = "A"                    // Optional. Default: "A"

    skipHeaders = true                  // Optional. Skip header line of each sheet (= first line of the sheet). Default: true
    addMissingKeys = true               // Optional. Add missing keys from sheet in wording files. If false, it will throw errors on default wording file when some keys are missing. Default true
    outputFormat = OutputFormat.ANDROID // Optional. The output format for wording files. Possible values are OutputFormat.ANDROID and OutputFormat.SPRING. Default: OutputFormat.ANDROID

    languages {
        register("default") {
            output = "src/main/resources/messages.properties" // Optional. Default: "src/main/res/values/strings.xml" for Android, "src/main/resources/messages.properties" for Spring.
            column = "B"                                      // *Required* The column containing the wording for the language.
        }
        register("fr") {
            output = "src/main/resources/messages_france.properties" // Optional, Default: "src/main/res/values-<LANGUAGE>/strings.xml" for Android, "src/main/resources/messages_<LANGUAGE>.properties" for Spring.
            column = "C"                                             // *Required* The column containing the wording for the language.
        }
        // [...] Add more languages here
    }
}
```

__Gradle < 8.2:__

```kotlin
wording {
    credentials.set("credentials.json")    // Optional. Default: use provided credentials  
    clientId.set("")                       // Optional. Default: use provided credentials  
    clientSecret.set("")                   // Optional. Default: use provided credentials  

    sheetId.set("THE SHEET ID")            // *Required*
    sheetNames.addAll("commons", "app")    // Optional. Default: use all sheetName
    filename.set("wording.xlsx")           // Optional. Default: "wording.xlsx"
    keysColumn.set("A")                    // Optional. Default: "A"

    skipHeaders.set(true)                  // Optional. Skip header line of each sheet (= first line of the sheet). Default: true
    addMissingKeys.set(true)               // Optional. Add missing keys from sheet in wording files. If false, it will throw errors on default wording file when some keys are missing. Default true
    outputFormat.set(OutputFormat.ANDROID) // Optional. The output format for wording files. Possible values are OutputFormat.ANDROID and OutputFormat.SPRING. Default: OutputFormat.ANDROID

    languages {
        register("default") {
            output.set("src/main/resources/messages.properties") // Optional. Default: "src/main/res/values/strings.xml" for Android, "src/main/resources/messages.properties" for Spring.
            column.set("B")                                      // *Required* The column containing the wording for the language.
        }
        register("fr") {
            output.set("src/main/resources/messages_france.properties") // Optional, Default: "src/main/res/values-<LANGUAGE>/strings.xml" for Android, "src/main/resources/messages_<LANGUAGE>.properties" for Spring.
            column.set("C")                                             // *Required* The column containing the wording for the language.
        }
        // [...] Add more languages here
    }
}
```

## Note

The plugin includes Google Project credentials for convenience use, but you can set up your own project. Create a new project 
in [GCP Console](https://console.cloud.google.com) then enable **Drive API** in *API library* and create credentials. You can use `credentials.json` file
or `clientId` / `clientSecret`.

## Known Issue

```bash
Execution failed for task ':app:updateWordingCa'.
> InputStream of class class org.apache.commons.compress.archivers.zip.ZipFile$1 is not implementing InputStreamStatistics.
```

You missed declaring the plugin in the root buildScript classpath:

```groovy
buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "com.betomorrow.gradle:gradle-spring-wording-plugin:3.1.0"
    }
}
```

# Dynamic Forms

Android app that renders dynamic forms from JSON Schema with validation.

## Overview

This app loads JSON schemas from a server and generates forms dynamically. Each schema defines the form fields, validation rules, and UI layout. Users can fill out forms with real-time validation and submit data.

## Project Structure

```
dynamic_forms_project/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/dynamic_forms_project/
│   │       │   ├── data/
│   │       │   │   ├── SchemaRepository.kt      # Loads schemas from API
│   │       │   │   ├── SchemaMetadata.kt        # Schema metadata model
│   │       │   │   └── SchemaParser.kt          # Parses JSON schemas
│   │       │   ├── ui/
│   │       │   │   ├── components/
│   │       │   │   │   └── JsonViewer.kt        # JSON viewer dialog
│   │       │   │   ├── screens/
│   │       │   │   │   ├── WelcomeScreen.kt     # Schema selection
│   │       │   │   │   ├── FormScreen.kt        # Dynamic form display
│   │       │   │   │   └── SuccessScreen.kt     # Submission success
│   │       │   │   ├── viewmodel/
│   │       │   │   │   └── FormViewModel.kt     # State management
│   │       │   │   └── AppNavigation.kt         # Navigation graph
│   │       │   └── MainActivity.kt
│   │       ├── res/
│   │       │   └── values/
│   │       │       ├── strings.xml              # Hebrew strings
│   │       │       ├── colors.xml
│   │       │       └── themes.xml
│   │       └── assets/
│   │           └── fallback_schema.json         # Local fallback schema
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml                       # Dependency versions
└── settings.gradle.kts
```

## Schema Creation

You can create and manage JSON schemas using the Schema Forge web tool:

**🔗 [https://schema-forge-607072911984.us-west1.run.app/](https://schema-forge-607072911984.us-west1.run.app/)**

## How to Run

1. Open the project in Android Studio
2. Sync Gradle dependencies
3. Run on an emulator or device (API 24+)
4. you can create your owm schemes in the provided link
5. the UI scheme is not provided from the server - i only implemented this on the local example. 

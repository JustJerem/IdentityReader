# Identity Reader

A modern application designed for scanning and processing identity documents such as passports,
French ID cards, and residence permits. The app leverages the **MVI pattern**, **multi-module
architecture**, and **Clean Architecture principles** for scalability and maintainability.

## Features

- **MRZ Strip Scanning**  
  Efficiently scans and extracts data from the MRZ strip on identity documents.

- **Document Type Recognition**  
  Identifies document types (e.g., passport, ID card, etc.) using Regex-based validation.

- **NFC Chip Integration**
  - Listens for NFC chip interactions.
  - Reads NFC chip data and securely transfers information.

- **Data Validation**  
  Validates extracted data to ensure accuracy and consistency.

- **Data Display**  
  Presents extracted and validated data in a user-friendly interface.

### Additional Highlights

- **Internationalization**  
  Fully localized for English, Spanish, and French.

- **Dark & Light Mode**  
  Supports both dark and light themes for an optimal user experience across various lighting
  conditions.

## Video

```html

<video width="100%" controls>
  <source src="https://raw.githubusercontent.com/JustJerem/IdentityReader/master/documentation/video.mp4"
          type="video/mp4">
  Your browser does not support the video tag.
</video>
```

## Screen Shots

[<img src="https://raw.githubusercontent.com/JustJerem/IdentityReader/master/documentation/Screenshot_0.png" width=200 />](https://raw.githubusercontent.com/JustJerem/IdentityReader/master/documentation/Screenshot_1.png)
[<img src="https://raw.githubusercontent.com/JustJerem/IdentityReader/master/documentation/Screenshot_1.png" width=200 />](https://raw.githubusercontent.com/JustJerem/IdentityReader/master/documentation/Screenshot_1.png)
[<img src="https://raw.githubusercontent.com/JustJerem/IdentityReader/master/documentation/Screenshot_2.png" width=200 />](https://raw.githubusercontent.com/JustJerem/IdentityReader/master/documentation/Screenshot_2.png)
[<img src="https://raw.githubusercontent.com/JustJerem/IdentityReader/master/documentation/Screenshot_4.png" width=200 />](https://raw.githubusercontent.com/JustJerem/IdentityReader/master/documentation/Screenshot_4.png)

## Screen Flow

![](https://raw.githubusercontent.com/JustJerem/IdentityReader/master/documentation/Flow4.jpg)

## Architecture Overview

[<img src="https://raw.githubusercontent.com/JustJerem/IdentityReader/master/documentation/Overview.png"/>](https://raw.githubusercontent.com/JustJerem/IdentityReader/master/documentation/Overview.png)

## 🛠 Built With

### Core Libraries

- [AndroidX Core KTX](https://developer.android.com/kotlin/ktx) - Extensions for Android core
  libraries to write concise and idiomatic Kotlin code.
- [Lifecycle Runtime KTX](https://developer.android.com/topic/libraries/architecture/lifecycle) -
  Kotlin extensions for Android lifecycle-aware components.
- [Timber](https://github.com/JakeWharton/timber) - A logger with a clean API for Android.

### User Interface

- [Compose BOM](https://developer.android.com/jetpack/compose/bom) - Manages consistent versions of
  Jetpack Compose dependencies.
- [Compose UI](https://developer.android.com/jetpack/compose/ui) - Toolkit for building declarative
  UI in Android.
  - [UI Graphics](https://developer.android.com/jetpack/compose/ui) - Library for managing Compose
    graphics.
  - [UI Tooling](https://developer.android.com/jetpack/compose/tooling) - Tools for UI previews and
    debugging.
  - [UI Tooling Preview](https://developer.android.com/jetpack/compose/tooling) - Preview and
    inspect UI directly in the IDE.
- [Material Icons Extended](https://developer.android.com/reference/androidx/compose/material/icons/package-summary) -
  A collection of extended Material Design icons for Compose applications.
- [Material 3](https://m3.material.io/) - Modern Material Design components for expressive UIs.

### Navigation

- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) - Simplifies and
  type-safe navigation in Compose.

### Camera & Media

- [CameraX](https://developer.android.com/training/camerax) - Camera library for simplified camera
  app development.
  - [Camera View](https://developer.android.com/training/camerax) - Provides a simple API for camera
    previews.
  - [Camera Core](https://developer.android.com/training/camerax) - Core functionality for camera
    features.
  - [Camera2](https://developer.android.com/training/camerax) - Backward compatibility with Camera2
    API.
  - [Camera Lifecycle](https://developer.android.com/training/camerax) - Lifecycle-aware components
    for cameras.
  - [Camera Video](https://developer.android.com/training/camerax) - Video recording support for
    CameraX.
  - [Camera Extensions](https://developer.android.com/training/camerax) - Enhances camera
    capabilities with effects.

### Machine Learning

- [ML Kit Text Recognition](https://developers.google.com/ml-kit/vision/text-recognition) - Machine
  learning for recognizing text in images.

### Testing

- [Espresso Core](https://developer.android.com/training/testing/espresso) - A framework for Android
  UI testing.
- [Espresso Intents](https://developer.android.com/training/testing/espresso) - Testing framework
  for Android UI, specialized for intent validation.
- [UI Test Manifest](https://developer.android.com/jetpack/compose/testing) - Simplifies testing
  Compose UI components.
- [UI Test JUnit4](https://developer.android.com/jetpack/compose/testing) - Compose UI testing with
  JUnit4.
- [JUnit](https://junit.org/junit5/) - Framework for unit testing in Java.
- [AndroidX JUnit](https://developer.android.com/testing) - Extensions for JUnit to test Android
  components.
- [Google Truth](https://truth.dev/) - Fluent assertion framework for testing.
- [MockK](https://mockk.io/) - Kotlin mocking framework for testing.

### Serialization

- [Kotlin Serialization JSON](https://github.com/Kotlin/kotlinx.serialization) - Library for JSON
  serialization in Kotlin.

### Security and Cryptography

- [Passport Analyzer (JMRTD)](https://github.com/eaubrey/jmrtd) - Library for reading
  machine-readable travel documents.
  - [Spongy Castle](https://rtyley.github.io/spongycastle/) - Cryptography API for Android.
  - [Scuba SC Android](https://sourceforge.net/projects/scuba/) - Tools for handling smart cards.


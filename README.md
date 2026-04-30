# SmartCaneApp 🦯

SmartCaneApp is an innovative Android application designed to pair with a smart cane, providing enhanced mobility, safety, and independence for the visually impaired or elderly. Built with modern Android development practices, it leverages real-time location tracking, fall detection, and turn-by-turn navigation.

## 🌟 Key Features

*   **Real-time Location Tracking**: Monitors the cane's location continuously using Google Maps integration.
*   **Intelligent Navigation**: Fetches optimized routes and turn-by-turn directions using the Google Directions API.
*   **Fall Detection Alerts**: Background services actively monitor for falls and trigger instant notifications via Firebase Cloud Messaging (FCM).
*   **Cloud Synchronization**: Seamlessly syncs user data, navigation routes, and cane status to Firebase Firestore in real time.
*   **Background Operations**: Ensures uninterrupted service even when the app is closed, using Android Foreground Services.
*   **Modern UI**: Fully built with **Jetpack Compose** for a smooth, reactive, and accessible user interface.

## 🛠️ Tech Stack

*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose
*   **Architecture**: MVVM (Model-View-ViewModel) with Coroutines and Flows
*   **Backend & Database**: Firebase (Firestore, Realtime Database, Storage, FCM)
*   **Networking**: Retrofit2 with Gson Converter
*   **Mapping & Location**: 
    *   Google Maps SDK for Android
    *   Google Play Services (Location)
    *   Google Maps Compose library
    *   Google Directions API

## 📋 Prerequisites

Before you begin, ensure you have met the following requirements:
*   Android Studio Ladybug (or newer).
*   Android SDK API Level 35.
*   A physical Android device or emulator running API level 24 or higher.
*   A valid Google Maps API Key.
*   A configured Firebase Project (`google-services.json`).

## 🚀 Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/SmartCaneApp.git
    cd SmartCaneApp
    ```

2.  **Add your Google Maps API Key:**
    *   Create a file named `local.properties` in the root directory if it doesn't exist.
    *   Add your API key to the file:
        ```properties
        MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY_HERE
        ```
    *   *Note: Never commit your `local.properties` to version control.*

3.  **Configure Firebase:**
    *   Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
    *   Add an Android app to your Firebase project using the package name `com.example.smartcaneapp`.
    *   Download the `google-services.json` file.
    *   Place the `google-services.json` file in the `app/` directory of this project.

4.  **Build and Run:**
    *   Open the project in Android Studio.
    *   Sync the project with Gradle files.
    *   Click the **Run** button to build and install the app on your device or emulator.

## 🏗️ Architecture Overview

SmartCaneApp follows the **MVVM (Model-View-ViewModel)** architectural pattern to separate UI logic from business logic.

*   **View**: Jetpack Compose UI components reactively observe state changes.
*   **ViewModel**: `LocationViewModel` and others manage UI state, interact with network interfaces (Retrofit), and handle background coroutines.
*   **Repository / Data Layer**: Interacts with Firebase Firestore and Google APIs to fetch and sync data.

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.

---
*Built with ❤️ for a more accessible world.*

# WeatherApp

WeatherApp is a comprehensive, modern Android weather forecasting application that provides real-time updates, detailed forecasts, and an AI-powered assistant (Gemini AI) to help users stay prepared.

## Features

- **Real-Time Weather:** Accurate weather retrieval based on precise GPS location or custom city search queries.
- **AI-Powered Assistant:** Integrated Google Gemini AI for interactive weather insights, personalized advice, and natural conversations.
- **Weather Alerts:** Automatic notifications for severe weather conditions, stored locally for easy tracking.
- **Interactive UI:** Smooth, intuitive navigation between current weather, detailed forecasts, and AI chat features.
- **Personalized Experience:** Persistent storage of user preferences (like search history and chosen city) using DataStore.

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin
- **Architecture Pattern:** MVVM (Model-View-ViewModel)
- **Local Database:** Room Database, DataStore Preferences
- **Network Layer:** Retrofit2, OkHttp3 (Logging Interceptor)
- **Asynchrony & Concurrency:** Coroutines, Kotlin Flow
- **Navigation:** Navigation Component
- **Image Loading:** Glide
- **AI Integration:** Google Generative AI (Gemini)
- **Location Services:** Google Play Services Location

## Getting Started

To run this project, you will need to add your API keys to the `local.properties` file in the root directory:

```properties
WEATHER_API_KEY="YOUR_WEATHER_API_KEY"
GEMINI_API_KEY="YOUR_GEMINI_API_KEY"
```

> **Note:** Get the Weather API Key from [WeatherAPI](https://www.weatherapi.com/) and the Gemini API key from [Google AI Studio](https://aistudio.google.com/).

1. Clone the repository.
2. Open the project in **Android Studio**.
3. Sync Gradle and build the app.
4. Run on an Android emulator or a physical device.

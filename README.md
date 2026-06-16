# MovieBlock Android

Native Android client for [MovieBlock](https://github.com/kedarnathdev/movieblock) - Automated seat booking for INOXMovies.

![Build & Release](https://github.com/kedarnathdev/movieblock-android/workflows/Android%20CI%20-%20Build%20%26%20Release/badge.svg)

## Features

- 📱 Native Android app built with Kotlin + Jetpack Compose
- 🎬 View movie details, showtimes, and theater info
- 🔄 Real-time task status updates with auto-refresh
- ⏱️ Countdown timers for cooldown periods
- 🎫 Update seat selections on-the-fly
- 🔔 Notification history tracking
- 🌙 Dark theme matching MovieBlock web design

## Screenshots

*Coming soon*

## Download

Download the latest APK from [Releases](https://github.com/kedarnathdev/movieblock-android/releases).

For development/testing, use the `app-debug.apk`.  
For production, use the `app-release-unsigned.apk` (you'll need to sign it yourself).

## Requirements

- Android 8.0 (API 26) or higher
- MovieBlock server running and accessible from your device

## Setup

1. Download and install the APK
2. Open the app and enter your MovieBlock server URL (e.g., `http://192.168.1.100:3001`)
3. Enter an INOX seat layout URL
4. Enter seat IDs (e.g., `B1, B2, C3`)
5. Tap "Start Automation"

## Building from Source

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

### Build Steps

```bash
# Clone the repository
git clone https://github.com/kedarnathdev/movieblock-android.git
cd movieblock-android

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

The APKs will be in `app/build/outputs/apk/`.

## Architecture

```
app/
├── src/main/java/com/kedarnathdev/movieblock/
│   ├── data/
│   │   ├── api/          # Retrofit API interface
│   │   ├── model/        # Data classes
│   │   └── repository/   # Repository layer
│   ├── ui/
│   │   ├── components/   # Reusable Composables
│   │   ├── screens/      # Screen Composables
│   │   ├── theme/        # Material 3 theme
│   │   └── viewmodel/    # ViewModels
│   └── MainActivity.kt
└── build.gradle.kts
```

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM |
| Networking | Retrofit + OkHttp |
| Async | Coroutines + Flow |
| Image Loading | Coil |
| DI | Manual (lightweight) |

## Design System

The app follows the [MovieBlock DESIGN.md](https://github.com/kedarnathdev/movieblock/blob/main/DESIGN.md) dark theme:

- **Canvas**: `#111110` - Dark background
- **Primary**: `#cc785c` - Coral accent for CTAs
- **Accent Teal**: `#5db8a6` - Status indicators
- **Accent Amber**: `#e8a55a` - Timers and warnings
- **Typography**: Cormorant Garamond (display) + Inter (body) + JetBrains Mono (code)

## API Compatibility

This app connects to the MovieBlock backend API:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/tasks` | GET | List all tasks |
| `/api/tasks` | POST | Create a task |
| `/api/tasks/:id` | GET | Get task details |
| `/api/tasks/:id/seats` | POST | Update seats |
| `/api/tasks/:id/stop` | POST | Stop a task |
| `/api/tasks/:id` | DELETE | Delete a task |

## License

[MIT](LICENSE) © Kedarnath Peraka

## Related

- [MovieBlock](https://github.com/kedarnathdev/movieblock) - Backend server

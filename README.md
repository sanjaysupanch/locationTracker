# Location Tracker

A robust Android application for continuous location tracking with offline support and automatic synchronization. The app tracks device location in the background using a foreground service, stores locations locally to ensure zero data loss, and automatically syncs with a remote server when network connectivity is available.

## Features

- **Continuous Location Tracking**: High-accuracy GPS location tracking using Google Play Services Location API
- **Foreground Service**: Runs as a foreground service with persistent notification to ensure reliable tracking
- **Offline Support**: All location data is stored locally using Room database, ensuring no data loss even when offline
- **Automatic Sync**: Background synchronization using WorkManager when network is available
- **Boot Receiver**: Automatically restarts tracking service after device reboot
- **Modern UI**: Built with Jetpack Compose and Material Design 3
- **Real-time Status**: View tracking status, network connectivity, and pending sync count
- **Zero Data Loss**: FIFO (First In First Out) sync strategy ensures all locations are synced in order

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Database**: Room (SQLite)
- **Networking**: Retrofit + Gson
- **Background Work**: WorkManager
- **Location Services**: Google Play Services Location API
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 36

## Architecture

The app follows MVVM architecture with clean separation of concerns:

```
app/src/main/java/com/example/tracking/
├── data/
│   ├── local/          # Room database entities and DAOs
│   ├── remote/         # API service and request models
│   └── repository/     # Data repository layer
├── domain/             # Business logic (LocationService)
├── ui/                 # Compose UI components and ViewModels
├── worker/             # WorkManager workers for background sync
├── di/                 # Dependency injection modules
└── util/               # Utility classes (BootReceiver, NetworkMonitor)
```

## Key Components

### LocationService
A foreground service that continuously tracks device location using FusedLocationProviderClient. It:
- Requests location updates every 5 seconds with high accuracy
- Stores each location in the local database
- Updates a persistent notification with current location
- Triggers background sync when new locations are saved

### LocationRepository
Manages location data operations:
- Saves locations to local Room database
- Syncs unsynced locations to remote server in FIFO order
- Tracks pending sync count

### SyncWorker
WorkManager worker that:
- Syncs all pending locations to the server
- Retries on failure with exponential backoff
- Only runs when network is available

### BootReceiver
BroadcastReceiver that automatically restarts the location tracking service when:
- Device boots up
- App is updated/replaced

## Permissions

The app requires the following permissions:

- `ACCESS_FINE_LOCATION` - For precise GPS location tracking
- `ACCESS_COARSE_LOCATION` - For approximate location (fallback)
- `INTERNET` - For syncing data to remote server
- `ACCESS_NETWORK_STATE` - To check network connectivity
- `FOREGROUND_SERVICE` - For running foreground service
- `FOREGROUND_SERVICE_LOCATION` - For location-based foreground service
- `POST_NOTIFICATIONS` - For showing tracking notification (Android 13+)

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK with API level 26+
- Google Play Services installed on device/emulator

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd locationTracker
```

2. Open the project in Android Studio

3. Sync Gradle files and wait for dependencies to download

4. Configure the API endpoint:
   - Update the base URL in `AppModule.kt` or your API configuration
   - Ensure your backend API endpoint is correctly configured

5. Build and run the app:
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio or use `./gradlew installDebug`

### Building

To build a debug APK:
```bash
./gradlew assembleDebug
```

To build a release APK:
```bash
./gradlew assembleRelease
```

The APK will be generated in `app/build/outputs/apk/`

## How It Works

1. **Start Tracking**: User taps "Start Tracking" button, which starts the `LocationService` foreground service
2. **Location Collection**: Service requests location updates every 5 seconds and stores them in Room database
3. **Local Storage**: Each location is immediately saved locally, ensuring zero data loss
4. **Background Sync**: When a new location is saved, a WorkManager sync job is triggered
5. **Network Check**: Sync worker only runs when network is available
6. **FIFO Sync**: Locations are synced to server in order (oldest first)
7. **Status Updates**: UI displays real-time tracking status, network connectivity, and pending sync count

## Project Structure

```
locationTracker/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/tracking/
│   │   │   │   ├── data/              # Data layer
│   │   │   │   ├── domain/            # Domain layer
│   │   │   │   ├── ui/                # UI layer
│   │   │   │   ├── worker/            # Background workers
│   │   │   │   ├── di/                # Dependency injection
│   │   │   │   └── util/              # Utilities
│   │   │   ├── res/                   # Resources
│   │   │   └── AndroidManifest.xml
│   │   └── test/                      # Unit tests
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml            # Dependency versions
├── build.gradle.kts
└── README.md
```

## Dependencies

Key dependencies used in this project:

- **Jetpack Compose**: Modern declarative UI toolkit
- **Hilt**: Dependency injection framework
- **Room**: Local database persistence
- **Retrofit**: HTTP client for API calls
- **WorkManager**: Background task scheduling
- **Google Play Services Location**: Location tracking API
- **Material Design 3**: Modern UI components

## Usage

1. **Grant Permissions**: On first launch, grant location and notification permissions
2. **Start Tracking**: Tap "Start Tracking" to begin location collection
3. **Monitor Status**: View tracking status, network connectivity, and pending sync count
4. **Stop Tracking**: Tap "Stop Tracking" to stop the location service

The app will continue tracking in the background even when the app is closed, and will automatically sync data when network is available.

## Notes

- Location tracking requires GPS to be enabled on the device
- Better accuracy is achieved when device has clear view of the sky
- Battery usage may be higher due to continuous GPS tracking
- The app automatically handles network connectivity changes
- All locations are stored locally first, then synced when possible

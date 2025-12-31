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

## Usage

1. **Grant Permissions**: On first launch, grant location and notification permissions
2. **Start Tracking**: Tap "Start Tracking" to begin location collection
3. **Monitor Status**: View tracking status, network connectivity, and pending sync count
4. **Stop Tracking**: Tap "Stop Tracking" to stop the location service

The app will continue tracking in the background even when the app is closed, and will automatically sync data when network is available.

## Demo
* https://drive.google.com/file/d/1ayWjOBHcxeXv_bnKKWQiSO-O0L9_R5sd/view?usp=sharing
* <img width="1080" height="2400" alt="Screenshot_20251228_003421" src="https://github.com/user-attachments/assets/49b2de0c-361f-4ee9-82e0-370cd989bcb0" />
* <img width="1080" height="2400" alt="Screenshot_20251228_003554" src="https://github.com/user-attachments/assets/49ad4ae4-5c1f-4280-a222-e2ea34baa618" />

## APK Link
* https://drive.google.com/file/d/1DM5xgY2nwdgkM4KBWvqyn1IMPCw2DTZX/view?usp=sharing
  

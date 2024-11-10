# SafeSpot: Sound-Based Emergency Detection App

SafeSpot is an Android application designed to enhance personal safety by detecting specific emergency sounds, such as screaming, and triggering an automatic emergency response. This project leverages audio detection to monitor ambient sounds, alerting emergency contacts or calling for help when a potential threat is detected. SafeSpot also uses device location to provide an approximate location during emergencies, ensuring timely assistance.

## Features

- **Sound Detection Service**: Continuously monitors ambient sounds using the microphone to detect emergency sounds like screams.
- **Automated Emergency Response**: Upon detecting a scream, the app can alert emergency contacts via SMS or call, and provide a notification.
- **Location Integration**: Retrieves the device’s current location (latitude and longitude) to include with emergency alerts, even functioning offline if no internet is available.
- **Background Operation**: Runs seamlessly in the background, keeping the user protected even when the app is closed.
- **Custom Notifications**: Sends high-priority notifications when an emergency sound is detected, ensuring user awareness and quick access.

## Technical Overview

- **Language**: Kotlin
- **Permissions**: 
  - Requires microphone access for sound detection.
  - Requires location access for emergency tracking.
- **Audio Processing**: Uses `AudioRecord` to capture audio samples and analyzes them in real-time for volume thresholds indicating a scream.
- **Background Service**: Implemented as a foreground service to continuously monitor sounds even when the device is idle.
- **Location Services**: Utilizes Google’s `FusedLocationProviderClient` to obtain location coordinates.

## Setup Instructions

1. Clone the repository and open it in Android Studio.
2. Grant necessary permissions in your Android manifest file:
   ```xml
   <uses-permission android:name="android.permission.RECORD_AUDIO" />
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   <uses-permission android:name="android.permission.CALL_PHONE" />

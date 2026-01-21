# ALVION

ALVION is an Android app that detects driver drowsiness and distraction using the phone’s front camera and on-device inference. The goal is real-time alerts (audio / vibration / visual) with privacy-first processing (no video sent to the cloud).

## Features (MVP)
- Start/stop a monitoring session
- Real-time drowsiness + distraction detection
- Alerts: sound, vibration, and visual UI feedback
- Settings to adjust thresholds and cooldown behavior
- Runs fully on-device (offline)

## Tech Stack
- Kotlin + Android Studio
- CameraX (camera preview + frame analysis)
- On-device inference (e.g., TFLite / NNAPI; optional Snapdragon acceleration depending on device)

## Getting Started
### Prerequisites
- Android Studio (latest stable)
- Android SDK installed
- An Android device or emulator

### Run
1. Clone the repo:
   ```bash
   git clone https://github.com/ch8see/Alvion.git
   cd Alvion
   
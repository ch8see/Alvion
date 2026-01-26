# ALVION

ALVION is an Android application that detects driver drowsiness and distraction in real-time using the device's front camera and on-device inference.

## ✨ Features (MVP)
- **Real-time Detection:** Monitor driver drowsiness and distraction.
- **Instant Alerts:** Configurable sound, vibration, and visual UI feedback.
- **Privacy First:** All processing runs fully on-device (offline); no video is sent to the cloud.
- **Customizable:** Settings to adjust thresholds and alert cooldown behavior.

## 🚀 Quick Start
### Prerequisites
- **Android Studio** (Latest stable version)
- **Android SDK** installed
- **Physical Android Device** (Recommended for camera and vibration testing)

### Setup & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/ch8see/Alvion.git
   cd Alvion
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle and run the `:app` module.

## 📁 Project Structure
- **/app**: The Android application source code (UI, Logic, and AI).
- **/doc**: Technical documentation, including design documents and LaTeX sources.
- **/.github**: Pull Request templates and team standards.

## 📖 Documentation
For a deep dive into the architecture, design decisions, and how to build the LaTeX technical documentation, see the [Documentation Guide](doc/README.md).

## 🛠 Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Camera:** CameraX (Core, View, Lifecycle)
- **Inference:** On-device processing (Privacy Focused)

---

## ⚖ License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

# Wafa Device Intelligence & Wafa ChargeLab

[![Android CI - Build Debug APK](https://github.com/Mehedi364/wafa-device-intelligence/actions/workflows/android-build.yml/badge.svg)](https://github.com/Mehedi364/wafa-device-intelligence/actions/workflows/android-build.yml)

**Developer:** Mehedi364 (Md. Mehedi Hasan)  
**Brand:** Wafa Zone  
**Package:** `site.wafazone.deviceintelligence`  
**Platform:** Native Android (Kotlin + Jetpack Compose + Material 3)  
**Database:** Room (Local SQLite)  
**CI/CD Pipeline:** Automated GitHub Actions  

---

## 📌 Executive Overview

**Wafa Device Intelligence** is an advanced native Android hardware diagnostics and system telemetry platform featuring the integrated **Wafa ChargeLab** analytics subsystem. 

### Core Product Principle
> **"Real device data. Honest diagnostics. No fabricated results."**  
Android unprivileged applications are prevented from accessing private battery cycles or flash controller wear endurance. Wafa Device Intelligence reports real, verified measurements from Android APIs and clearly indicates when values are unsupported or restricted by OS security boundaries.

---

## ⚡ Key Modules & Features

### 1. Technical Command Center (Dashboard)
- Real-time device identity: Model, Manufacturer, Brand, Board, Hardware, Security Patch, and Android release.
- Live system telemetry cards: Battery status, CPU architecture, RAM memory, Storage volume, Network transport, Thermal state, and discovered sensor count.
- One-Tap Full Device Check launcher.

### 2. Wafa ChargeLab (Battery & Charging Analytics)
- High-fidelity animated circular battery gauge with live terminal voltage and temperature.
- Real-time charging state detection (`AC`, `USB`, `Wireless`, `Dock`, `Unplugged`).
- Active charging session tracking: Duration, initial vs. current percentage, min/max thermistor readings, min/max voltage.
- Estimated charging rate in percentage points gained per hour (`+X%/hr`) and ETA to full with uncertainty indication.
- Historical charging sessions persisted locally in Room database with session comparison summaries.
- Optional foreground service for continuous charging monitoring.

### 3. Wafa Hardware Test Lab
- **Display Color Screen Test:** Full-screen solid red, green, blue, white, black, yellow, cyan, and magenta to manually identify stuck or dead pixels.
- **Gradient Banding Check:** Smooth monochrome gradient to inspect display color-depth stepping.
- **Interactive Touch-Grid Matrix:** Multi-cell interactive matrix covering the full display canvas to verify touchscreen digitizer responsiveness and corner touch sensitivity.
- **Speaker Playback Verification:** Generates real 16-bit PCM 440 Hz (concert pitch A) acoustic sine wave using `AudioTrack`.
- **Microphone Real-Time VU Meter:** Captures audio through `AudioRecord` and streams calculated Root-Mean-Square (RMS) amplitude and decibels.
- **Vibration Haptic Test:** Triggers multi-pulse waveform haptic feedback via `Vibrator` / `VibratorManager`.
- **Camera Flashlight:** Toggles rear optical LED torch via `CameraManager`.

### 4. Sensor Calibration Assistant
- Dynamic enumeration of all hardware and virtual sensors via Android `SensorManager`.
- Live real-time streams for Accelerometer ($m/s^2$), Gyroscope ($rad/s$), Magnetometer/Compass ($\mu T$), Ambient Light ($lx$), Proximity ($cm$), and Barometer ($hPa$).
- Sensor anomaly and freeze detection.

### 5. Network Quality Lab
- Real-time active transport detection (`Wi-Fi`, `Cellular`, `Ethernet`, `VPN`, `Metered`, `Validated`).
- Real DNS resolution test measuring actual round-trip latency against `dns.google` in milliseconds.
- Real HTTP ping and jitter benchmark measuring 3 consecutive requests to detect network latency variance.
- Local IPv4 and IPv6 network interface address inspector.

### 6. Thermal & Performance Correlation
- Real-time Android `PowerManager` thermal status tracking (`NOMINAL`, `LIGHT`, `MODERATE`, `SEVERE`, `CRITICAL`, `EMERGENCY`, `SHUTDOWN`).
- Bounded, repeatable CPU mathematical benchmark (Prime Sieve of Eratosthenes across 120,000 integers).
- Memory buffer throughput benchmark allocating and transforming 2.5M float buffer elements.
- Before-and-after correlation displaying whether thermal throttling occurred during computation.
- Benchmark history persisted in Room database.

### 7. Storage Intelligence
- Partition telemetry via `StatFs`: Total capacity, used space, and free space.
- Application-isolated sandbox storage breakdown: Internal cache, external cache, and app data directory sizes.
- Clear technical explanation of eMMC / UFS physical flash wear restrictions.

### 8. Smart Diagnostic Reports & Unified Timeline
- Export full technical diagnostic reports in **JSON**, **CSV**, and **Printable Text** formats.
- Native Android Share Sheet integration via `FileProvider` (`site.wafazone.deviceintelligence.fileprovider`).
- Unified Device Timeline consolidating diagnostic runs, charging sessions, and benchmarks.

### 9. Premium Personalization & Theme System
Four meticulously designed laboratory color palettes:
- **Obsidian Aurora (Default):** Deep obsidian (`#0B0D10`), champagne gold (`#D6B46A`), and electric blue (`#4DA3FF`).
- **Graphite Pro:** Deep graphite (`#121418`), neutral silver (`#D0D5DD`), and cool technical blue.
- **Light Titanium:** Clean titanium white (`#F4F5F8`) with dark graphite text.
- **Midnight Violet:** Deep navy-black (`#090A12`) with electric violet accents.
- Theme preferences automatically persisted across app restarts via Jetpack DataStore.

---

## 🏗 Architecture & Code Structure

```text
site.wafazone.deviceintelligence (com.example)
│
├── data/
│   ├── local/
│   │   ├── dao/             # Room DAOs (ChargingSessionDao, DiagnosticHistoryDao, BenchmarkHistoryDao)
│   │   ├── entity/          # Room Entities (Charging, History, Benchmark)
│   │   ├── ThemePreferences # Jetpack DataStore Theme Mode Repository
│   │   └── WafaDatabase     # Room Database Instance
│   └── model/               # Immutable Domain Models (BatteryInfo, DiagnosticResult, etc.)
│
├── engine/
│   ├── BatteryEngine        # BatteryManager & Charging Session Tracker
│   ├── HardwareEngine       # Build, ActivityManager RAM, StatFs, CameraManager
│   ├── HardwareTestLabEngine# AudioTrack PCM tone, AudioRecord RMS, Vibrator, Torch
│   ├── NetworkEngine        # ConnectivityManager, DNS lookup, HTTP ping & jitter
│   ├── PerformanceEngine    # CPU Prime Sieve & Memory throughput benchmarks
│   ├── ReportEngine         # JSON, CSV, Text generator & FileProvider sharing
│   ├── SensorEngine         # SensorManager enumeration & live streams
│   └── ThermalEngine        # PowerManager thermal status listener
│
├── service/
│   └── BatteryMonitorService# Foreground service for continuous charging monitoring
│
├── ui/
│   ├── components/          # Reusable M3 Components (WafaTopBar, WafaBatteryGauge, etc.)
│   ├── navigation/          # Screen routes and WafaBottomNav
│   ├── screens/             # Jetpack Compose Screens (Dashboard, ChargeLab, Lab, etc.)
│   └── theme/               # Obsidian, Graphite, Titanium & Midnight Violet Themes
│
├── WafaApplication.kt       # Application Singleton
└── MainActivity.kt          # Edge-to-Edge Container & Screen Router
```

---

## 🛠 Local Build Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or JDK 21 (Temurin / Eclipse Adoptium recommended)
- Android SDK Platform 34 or newer

### Building with Gradle
To compile and assemble the debug APK:
```bash
./gradlew assembleDebug
```

The generated APK will be located at:
```text
app/build/outputs/apk/debug/app-debug.apk
```

### Running Unit Tests & Robolectric
```bash
./gradlew :app:testDebugUnitTest
```

---

## 🚀 Automated GitHub Actions CI/CD Pipeline

The repository includes `.github/workflows/android-build.yml` which executes on every push:
1. Sets up JDK 21 and the Android SDK environment.
2. Resolves all Gradle dependencies and validates wrappers.
3. Executes unit tests and Robolectric test suites.
4. Executes `./gradlew assembleDebug` to build the real APK.
5. Verifies APK file size, structure, and integrity (`unzip -t`).
6. Copies the verified APK to:
   - `.build-outputs/app-debug.apk`
   - `APK_DOWNLOAD/app-debug.apk`
7. Computes and verifies SHA256 checksums.
8. Uploads `app-debug-apk` as a downloadable GitHub Actions artifact.
9. Automatically commits `APK_DOWNLOAD/app-debug.apk` to allow direct download via GitHub Code Browser.

---

## 🔒 Permissions & Security

| Permission | Type | Justification |
| :--- | :--- | :--- |
| `INTERNET` | Normal | Required for DNS latency and HTTP ping tests |
| `ACCESS_NETWORK_STATE` | Normal | Required for checking Wi-Fi / Cellular transports |
| `VIBRATE` | Normal | Required for the Vibration hardware test |
| `CAMERA` | Runtime | Used only during interactive Camera & Flashlight tests |
| `RECORD_AUDIO` | Runtime | Used only during live microphone RMS waveform testing |
| `POST_NOTIFICATIONS` | Runtime | Required for foreground charging monitor notification |
| `FOREGROUND_SERVICE` | Install | Supports user-initiated background battery monitoring |

---

## 👨‍💻 Developer Attribution

- **Developer:** Mehedi364 (Md. Mehedi Hasan)
- **Brand:** Wafa Zone
- **Project:** Wafa Device Intelligence with Wafa ChargeLab
- **License:** Open Source / MIT

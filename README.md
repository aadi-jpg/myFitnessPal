# 📱 MyFitnessPal Clone (Android)

An Android fitness tracking application built with **Kotlin, Jetpack Compose, Firebase, and Room**.  
This app allows users to log workouts, track nutrition, monitor weight progress, manage fitness plans, and update their profile — all with **real-time sync using Firebase Firestore**.

---

## ✨ Features

### 🎯 Core Features
- 📊 **Dashboard** – View calories consumed, burned, and workouts logged (auto-updates daily).
- 🏋️ **Workouts** – Log workouts (type, duration, calories burned).
- 🍎 **Nutrition** – Log meals (food, calories, protein, carbs, fats).
- 📈 **Progress** – Track weight progress over time with history and charts.
- 📝 **Plans** – Create and manage personalized fitness plans.
- 👤 **Profile** – Update personal details (name, age, height, weight, goals).
- 🔐 **Authentication** – Secure login and registration using **Firebase Auth**.
- 🚀 **Logout** – Safely log out and return to login screen.

### 🔔 Real-time Updates
- Calories consumed/burned update in **real-time** using Firestore snapshot listeners.
- Data resets daily (new logs each day).

---

## 🎨 Design & UI
- Built with **Jetpack Compose (Material 3)** for a modern and clean UI.
- Responsive layouts for all screen sizes.
- Navigation via **Bottom Navigation Bar** with **NavGraph**.

---

## 🛠️ Technologies Used

### Frontend
- **Kotlin** – Primary development language.
- **Jetpack Compose** – Declarative UI toolkit.
- **Material 3** – Modern Android design system.

### Backend & Database
- **Firebase Authentication** – User sign-up and login.
- **Firebase Firestore** – Cloud NoSQL database for user data, workouts, nutrition, and progress.

### Libraries
- **Navigation Compose** – For navigation between screens.
- **MPAndroidChart** – Graphs for progress visualization.
- **JitPack** – External library hosting.

---

## 📁 Project Structure
```plaintext
myFitnessPal/
├── app/src/main/java/com/example/myfitnesspal/
│   ├── data/               # Data models & repository
│   │   ├── User.kt
│   │   ├── Workout.kt
│   │   ├── Meal.kt
│   │   ├── ProgressEntry.kt
│   │   └── UserRepository.kt
│   │
│   ├── ui/
│   │   ├── navigation/      # NavGraph & BottomNav
│   │   └── screens/         # Composable screens
│   │       ├── DashboardScreen.kt
│   │       ├── WorkoutsScreen.kt
│   │       ├── NutritionScreen.kt
│   │       ├── ProgressScreen.kt
│   │       ├── ProfileScreen.kt
│   │       └── PlanScreen.kt
│   │
│   ├── LoginActivity.kt        # Firebase Login (XML-based)
│   ├── RegistrationActivity.kt # Firebase Registration (XML-based)
│   └── MainActivity.kt         # Entry point (Compose NavGraph)
│
├── res/layout/              # XML layouts (Login, Registration)
├── build.gradle             # App Gradle config
├── settings.gradle.kts      # JitPack + repositories
└── README.md                # Documentation
```

---

---

---

## 🚀 Getting Started

### ✅ Prerequisites
Before running this project, ensure you have the following installed and configured:

- **Android Studio** (latest version recommended)  
- **Java 17+** installed on your system  
- **Kotlin plugin** enabled in Android Studio  
- A **Firebase project** with:
  - **Firebase Authentication** (Email & Password)  
  - **Firestore Database** enabled  

---

## ⚡ Installation (continued)

1. **Open the project in Android Studio**  
   - Go to **File > Open**  
   - Select the cloned folder `myFitnessPal`

2. **Sync Gradle dependencies**

./gradlew build

3. **Configure Firebase**  
   - Download your `google-services.json` file from **Firebase Console**  
   - Place it inside the **app/** directory  
   - Enable **Authentication** and **Firestore** in Firebase Console  

4. **Run the project**
   ```bash
   ./gradlew installDebug

---

## 🎨 Design & UI
- 📱 Optimized for a **mobile-first experience**  
- Supports both **portrait & landscape orientations**  
- Styled with **Material 3 theming** for a modern look  
- **Responsive layouts** that adapt to various screen sizes & resolutions  

---

## 🎯 Key Screens
- 📊 **Dashboard** – View calories consumed, calories burned, and workouts logged daily.  
- 🏋️ **Workouts** – Log workouts (type, duration, calories burned).  
- 🍎 **Nutrition** – Add meals (food, calories, protein, carbs, fats).  
- 📈 **Progress** – Track weight history and visualize progress with charts.  
- 📝 **Plans** – Store and manage custom fitness plans.  
- 👤 **Profile** – Edit personal details and log out securely.  

---

## 🔗 Firebase Integration

### 🔐 Authentication
- Email & Password based secure login & registration  

### 📊 Firestore Database
- Data is structured into sub-collections per user:  
  - `users/{uid}/workouts`  
  - `users/{uid}/nutrition`  
  - `users/{uid}/progress`  
  - `users/{uid}/plans`  

➡️ Each section is tied to a **date field (yyyy-MM-dd)** to ensure **daily reset**.  

---

## 📦 Deployment
- Run directly on an **Android Emulator** or a **physical Android device** via Android Studio  
- Export as an **APK** for installation and sharing  

---

## 👨‍💻 Author
**Aadi**  
GitHub: [@aadi-jpg](https://github.com/aadi-jpg)

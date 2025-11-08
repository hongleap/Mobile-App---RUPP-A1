# E-Commerce Mobile App

A modern e-commerce mobile application built with Android Jetpack Compose and Firebase.

## 📋 Prerequisites

**Required Software:**
- Android Studio (latest version)
- JDK 17 or higher
- Kotlin (included with Android Studio)
- Android SDK (installed via Android Studio)

**Accounts & Keys:**
- Firebase account
- Gemini API key (for AI chatbot feature) - Get from: https://makersuite.google.com/app/apikey

**Knowledge:**
- Basic Android development
- Kotlin programming
- Jetpack Compose (for UI)

## 🚀 Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/hongleap/Mobile-App---RUPP-A1.git
cd APP
```

### 2. Firebase Configuration

#### For Android App:
You will receive `google-services.json` from the team lead via Telegram.

1. Place the `google-services.json` file in the `app/` folder:
   ```
   app/google-services.json
   ```

2. The file should already be in the correct location. If not, copy it there.

#### For Web Dashboard:
You will receive `config.js` from the team lead via Telegram.

1. Place the `config.js` file in the `web/` folder:
   ```
   web/config.js
   ```

2. The file should already be in the correct location. If not, copy it there.

### 3. Gemini API Key Setup

Each team member should use their own Gemini API key:

1. Get your API key from: https://makersuite.google.com/app/apikey

2. Open `app/src/main/java/com/example/app/chat/config/ApiConfig.kt`

3. Replace `"YOUR_GEMINI_API_KEY"` with your actual API key:
   ```kotlin
   const val GEMINI_API_KEY = "your-actual-api-key-here"
   ```

### 4. Build and Run Mobile App

1. Open the project in Android Studio

2. Sync Gradle files (Android Studio will do this automatically)

3. Connect an Android device or start an emulator

4. Click "Run" or press `Shift + F10`

### 5. Web Admin Dashboard

The `web/` folder contains the admin dashboard for managing products.

**To use the web dashboard:**

1. Make sure you have `web/config.js` (received from team lead via Telegram)

2. Open `web/admin-login.html` in your browser

3. Or run a local server:
   ```bash
   cd web
   python3 -m http.server 8000
   ```
   Then open: `http://localhost:8000/admin-login.html`

**Note:** The web dashboard is separate from the mobile app and works independently.

## 📁 Project Structure

```
APP/
├── app/                    # Android app
│   ├── src/main/java/      # Kotlin source code
│   └── google-services.json # Firebase config (from team lead)
├── web/                    # Web admin dashboard
│   ├── config.js          # Firebase config (from team lead)
│   └── *.html             # Admin dashboard files
└── README.md              # This file
```

## 🔐 Important Files

**Do NOT commit these files to Git:**
- `app/google-services.json` - Contains Firebase API keys
- `web/config.js` - Contains Firebase API keys
- `app/src/main/java/com/example/app/chat/config/ApiConfig.kt` - Contains your Gemini API key

**These files are safe to commit:**
- `app/google-services.json.example` - Template file
- `web/config.js.example` - Template file

## 📱 Features

- User authentication (Email/Password)
- Product browsing and search
- Shopping cart
- Order management
- AI chatbot assistant
- Admin dashboard (web)

## 🛠️ Technologies

- **Android**: Jetpack Compose, Kotlin
- **Backend**: Firebase (Authentication, Firestore, Storage)
- **AI**: Google Gemini API
- **Web Dashboard**: HTML, JavaScript, Firebase Web SDK

## 📞 Support

If you have any questions or need help:
- Contact the team lead for Firebase configuration files (`google-services.json` and `config.js`)

## 📝 Notes

- Make sure you have internet connection for Firebase services
- The AI chatbot requires a valid Gemini API key
- The web dashboard is separate from the mobile app and can be run independently


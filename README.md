# E-Commerce Mobile App with Microservices Backend

A modern e-commerce application with an Android mobile app and microservices backend architecture.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Local Setup](#local-setup)
  - [1. Android App Setup](#1-android-app-setup)
  - [2. Backend Services Setup](#2-backend-services-setup)
  - [3. Web Dashboard Setup](#3-web-dashboard-setup-optional)
- [Running the Application](#running-the-application)
- [Testing Locally](#testing-locally)
- [Architecture Overview](#architecture-overview)
- [Troubleshooting](#troubleshooting)

## 📱 Project Overview

This project consists of three main components:

1. **Android Mobile App** (`app/`) - User-facing mobile application
2. **Microservices Backend** (`services/`) - Backend API services
3. **Web Admin Dashboard** (`web/`) - Admin interface for product management

### Features

- User authentication (Email/Password)
- Product browsing and search
- Shopping cart
- Order management
- Real-time notifications
- AI chatbot assistant (Gemini)
- Admin dashboard (web)
- **Blockchain Integration**:
  - CToken (ERC-20) payment system on Sepolia testnet
  - MetaMask wallet integration
  - Token-based purchases
  - Transaction history tracking
  - Replay attack prevention
  - Server-side transaction verification

## 🔧 Prerequisites

### Required Software

- **Android Studio** (latest version) - For Android app development
- **JDK 17 or higher** - For backend services
- **Docker & Docker Compose** - For running backend services (recommended)
- **Git** - For version control

### Required Accounts & Keys

- **Firebase Account** - For authentication and database
- **Gemini API Key** (optional) - For AI chatbot feature
  - Get from: https://makersuite.google.com/app/apikey

### Required Configuration Files

You'll need these files from your team lead:

1. `app/google-services.json` - Firebase config for Android app
2. `web/config.js` - Firebase config for web dashboard
3. `services/firebase-service-account.json` - Firebase service account for backend

## 📁 Project Structure

```
APP/
├── app/                          # Android Mobile App
│   ├── src/main/java/           # Kotlin source code
│   ├── google-services.json      # Firebase config (get from team lead)
│   └── google-services.json.example
│
├── services/                     # Microservices Backend
│   ├── api-gateway/             # API Gateway (Port 8080)
│   ├── product-service/         # Product Service (Port 9091)
│   ├── order-service/           # Order Service (Port 9092)
│   ├── notification-service/    # Notification Service (Port 9093)
│   ├── transaction-service/     # Transaction Service (Port 9094)
│   ├── shared-models/           # Shared data models
│   ├── docker-compose.yml       # Docker configuration
│   ├── firebase-service-account.json  # Firebase config (get from team lead)
│   └── firebase-service-account.json.example
│
└── web/                         # Web Admin Dashboard
    ├── admin-login.html
    ├── admin-dashboard.html
    ├── config.js                # Firebase config (get from team lead)
    └── config.js.example
```

## 🚀 Local Setup

### 1. Android App Setup

#### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd APP
```

#### Step 2: Configure Firebase

1. Get `google-services.json` from your team lead
2. Place it in the `app/` folder:
   ```
   app/google-services.json
   ```

#### Step 3: Configure Gemini API Key (Optional)

1. Get your API key from: https://makersuite.google.com/app/apikey
2. Open `app/src/main/java/com/example/app/chat/config/ApiConfig.kt`
3. Replace `"YOUR_GEMINI_API_KEY"` with your actual API key:
   ```kotlin
   const val GEMINI_API_KEY = "your-actual-api-key-here"
   ```

#### Step 4: Update API URL for Local Development

1. Open `app/src/main/java/com/example/app/api/ApiClient.kt`
2. Ensure the BASE_URL is set for local development:
   ```kotlin
   // For Android Emulator
   private const val BASE_URL = "http://10.0.2.2:8080"
   
   // For Physical Device (replace with your computer's IP)
   // private const val BASE_URL = "http://192.168.1.XXX:8080"
   ```

   **To find your computer's IP:**
   - **Mac/Linux**: Run `ifconfig | grep "inet "`
   - **Windows**: Run `ipconfig`

#### Step 5: Build and Run

1. Open the project in Android Studio
2. Sync Gradle files (Android Studio will do this automatically)
3. Connect an Android device or start an emulator
4. Click "Run" or press `Shift + F10`

### 2. Backend Services Setup

#### Step 1: Get Firebase Service Account Key

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to **Project Settings** → **Service Accounts**
4. Click **"Generate New Private Key"**
5. Save the JSON file as `firebase-service-account.json` in the `services/` directory

#### Step 2: Choose Setup Method

**Option A: Docker (Recommended - Easiest)**

```bash
cd services

# Make sure firebase-service-account.json is in this directory
# Then run:
docker-compose up --build
```

Or use the convenience script:
```bash
cd services
chmod +x start-services.sh
./start-services.sh
```

**Option B: Local Development (Without Docker)**

```bash
cd services

# Build all services
./gradlew build

# Run each service in separate terminals:

# Terminal 1 - Product Service
cd product-service && ./gradlew run

# Terminal 2 - Order Service
cd order-service && ./gradlew run

# Terminal 3 - Notification Service
cd notification-service && ./gradlew run

# Terminal 4 - API Gateway
cd api-gateway && ./gradlew run
```

#### Step 3: Verify Services are Running

After starting services, verify they're running:

```bash
# Test API Gateway (main entry point)
curl http://localhost:9090/

# Test Product Service
curl http://localhost:9090/api/products

# You should see JSON response with products
```

**Service URLs:**
- **API Gateway**: `http://localhost:8080` ⭐ **Use this for all requests**
- Product Service: `http://localhost:9091` (direct access - debugging only)
- Order Service: `http://localhost:9092` (direct access - debugging only)
- Notification Service: `http://localhost:9093` (direct access - debugging only)
- Transaction Service: `http://localhost:9094` (direct access - debugging only)

⚠️ **Important**: Always use the API Gateway (port 8080) for client requests. Direct access to services bypasses authentication.

### 3. Web Dashboard Setup (Optional)

#### Step 1: Configure Firebase

1. Get `config.js` from your team lead
2. Place it in the `web/` folder:
   ```
   web/config.js
   ```

#### Step 2: Run Web Dashboard

**Option A: Open directly in browser**
```bash
# Simply open the HTML files in your browser
open web/admin-login.html
```

**Option B: Run local server**
```bash
cd web
python3 -m http.server 8000
# Then open: http://localhost:8000/admin-login.html
```

## 🏃 Running the Application

### Complete Local Setup Flow

1. **Start Backend Services** (Terminal 1)
   ```bash
   cd services
   docker-compose up --build
   ```
   Wait until you see all services started successfully.

2. **Run Android App** (Android Studio)
   - Open project in Android Studio
   - Connect device/emulator
   - Click Run

3. **Access Web Dashboard** (Optional)
   ```bash
   cd web
   python3 -m http.server 8000
   # Open http://localhost:8000/admin-login.html
   ```

### How It Works Together

```
┌─────────────────────────────────────────────────────────┐
│              Android App (app/)                        │
│              - Runs on phone/emulator                  │
│              - Connects to API Gateway                 │
└─────────────────────────────────────────────────────────┘
                        │
                        │ HTTP Requests
                        │ http://10.0.2.2:8080
                        ▼
┌─────────────────────────────────────────────────────────┐
│              API Gateway (Port 8080)                    │
│              - Receives requests from app               │
│              - Routes to appropriate service            │
│              - Handles authentication                   │
└─────────────────────────────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┬───────────────┐
        │               │               │               │
        ▼               ▼               ▼               ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│   Product    │  │    Order     │  │ Notification │  │ Transaction  │
│   Service    │  │   Service    │  │   Service    │  │   Service    │
│  (Port 9091) │  │  (Port 9092) │  │  (Port 9093) │  │  (Port 9094) │
└──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘
        │               │               │               │
        └───────────────┼───────────────┴───────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────┐
│              Firebase (Cloud Database)                 │
│              - Stores all data                         │
│              - Authentication                           │
│              - Transaction history                      │
└─────────────────────────────────────────────────────────┘
                        │
                        │ (Transaction Service also connects to)
                        ▼
┌─────────────────────────────────────────────────────────┐
│         Ethereum Sepolia Testnet (Blockchain)          │
│         - CToken (ERC-20) smart contract               │
│         - Transaction verification                      │
│         - Immutable transaction records                 │
└─────────────────────────────────────────────────────────┘
```

## 🧪 Testing Locally

### Test Backend Services

```bash
# Test API Gateway root endpoint
curl http://localhost:9090/

# Test Product Service (via API Gateway)
curl http://localhost:9090/api/products

# Test Product by ID (replace PRODUCT_ID with actual ID)
curl http://localhost:9090/api/products/PRODUCT_ID

# Test Products by Category
curl http://localhost:9090/api/products/category/Hoodies
```

### Test Android App

1. Run the app on emulator or device
2. Navigate through screens:
   - Home screen should show products
   - Product details should load
   - Cart should work
   - Orders should be accessible

### Test Web Dashboard

1. Open `http://localhost:8000/admin-login.html`
2. Login with admin credentials
3. Add/edit products
4. View orders

## 🏗️ Architecture Overview

### Microservices Architecture

The backend uses a microservices architecture with:

- **API Gateway** - Single entry point, handles authentication and routing
- **Product Service** - Manages product catalog and stock
- **Order Service** - Handles order creation and management
- **Notification Service** - Manages user notifications

### Technology Stack

**Android App:**
- Kotlin
- Jetpack Compose
- Firebase SDK
- OkHttp (for API calls)

**Backend Services:**
- Kotlin
- Ktor (HTTP server framework)
- Firebase Admin SDK
- Docker (containerization)

**Web Dashboard:**
- HTML/CSS/JavaScript
- Firebase Web SDK

**Database:**
- Firebase Firestore (NoSQL database)

### Blockchain Integration

**CToken Payment System:**
- **Smart Contract**: ERC-20 token deployed on Ethereum Sepolia testnet
- **Token Symbol**: CLOT
- **Wallet Integration**: MetaMask for Android
- **Network**: Sepolia Testnet (Chain ID: 11155111)

**Transaction Service Features:**
1. **Transaction Tracking**:
   - Stores transaction hashes in Firestore (`consumed_transactions` collection)
   - Prevents replay attacks by checking if transaction already used
   - Validates transactions on blockchain before accepting

2. **Transaction History**:
   - Syncs transaction history across devices
   - Stores in Firestore (`transactions` collection)
   - Local caching for offline access

3. **Payment Verification**:
   - Verifies token transfers on blockchain
   - Checks transaction amount and recipient
   - Validates transaction status (confirmed/pending)

**API Endpoints** (via Transaction Service):
- `POST /api/transactions/mark-consumed` - Mark transaction as used
- `GET /api/transactions/check/:hash` - Check if transaction consumed
- `POST /api/transactions/save` - Save transaction to history
- `GET /api/transactions/history` - Get user's transaction history

## 🔍 Troubleshooting

### Backend Services Won't Start

**Problem**: Ports already in use
```bash
# Check if ports are in use
lsof -i :9090  # Mac/Linux
netstat -ano | findstr :9090  # Windows

# Solution: Stop other services using these ports or change ports in docker-compose.yml
```

**Problem**: Firebase service account not found
```bash
# Solution: Ensure firebase-service-account.json exists in services/ directory
ls services/firebase-service-account.json
```

**Problem**: Docker not running
```bash
# Solution: Start Docker Desktop or Docker daemon
# Then try again: docker-compose up --build
```

### Android App Can't Connect to Backend

**Problem**: "Connection refused" error
```bash
# Solution 1: Ensure backend services are running
curl http://localhost:9090/

# Solution 2: Check API URL in ApiClient.kt
# For emulator: http://10.0.2.2:9090
# For physical device: http://YOUR_COMPUTER_IP:9090

# Solution 3: Check network security config
# Ensure app/src/main/res/xml/network_security_config.xml allows HTTP
```

**Problem**: "Unauthorized" errors
```bash
# Solution: Ensure user is logged in via Firebase Auth
# Check Firebase authentication in app
```

### Products Don't Load

**Problem**: Empty product list
```bash
# Solution 1: Check if Firebase has products
# Use web dashboard to add products

# Solution 2: Check API Gateway logs
docker-compose logs api-gateway

# Solution 3: Test Product Service directly
curl http://localhost:9091/api/products
```

### Firebase Connection Errors

**Problem**: Firebase initialization fails
```bash
# Solution 1: Verify google-services.json is correct
# Solution 2: Check Firebase project settings
# Solution 3: Ensure internet connection
```

## 📝 Important Notes

### Files NOT to Commit

**Never commit these files to Git:**
- `app/google-services.json` - Contains Firebase API keys
- `web/config.js` - Contains Firebase API keys
- `services/firebase-service-account.json` - Contains service account credentials
- `app/src/main/java/com/example/app/chat/config/ApiConfig.kt` - Contains Gemini API key

**Safe to commit:**
- `*.example` files - Template files
- All source code (except API keys)

### Development vs Production

**Current Setup (Local Development):**
- Services run on `localhost:9090`
- Android app connects to `http://10.0.2.2:9090` (emulator) or your computer's IP
- All services exposed for debugging

**Production (Future):**
- Services deployed to cloud (one platform)
- Android app connects to `https://api.yourapp.com`
- Only API Gateway exposed publicly

## 👥 For Team Members

### First Time Setup

1. Clone the repository
2. Get configuration files from team lead:
   - `app/google-services.json`
   - `web/config.js`
   - `services/firebase-service-account.json`
3. Get your own Gemini API key (optional)
4. Follow setup instructions above

### Daily Development

1. Start backend services: `cd services && docker-compose up`
2. Run Android app from Android Studio
3. Make changes and test locally

### Getting Help

- Check this README first
- Check `services/README.md` for backend details
- Contact team lead for configuration files
- Check Firebase Console for database issues

## 📚 Additional Resources

- [Ktor Documentation](https://ktor.io/)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Docker Documentation](https://docs.docker.com/)

---

**Happy Coding! 🚀**

# 🛍️ Clot E-Commerce Ecosystem

A professional, full-stack e-commerce solution featuring a **Kotlin Android App**, a **Node.js Microservices Backend**, and a **React Admin Dashboard**.

---

## 🚀 Quick Start (Get Running in 5 Mins)

### 1. Backend (The Brain)
```bash
cd services
# 1. Place your firebase-service-account.json here
# 2. Start everything with one command
docker compose up --build -d
```
*Backend is now live at `http://localhost:8080`*

### 2. Admin Dashboard (The Control Room)
```bash
cd web-admin
npm install
npm run dev
```
*Dashboard is now live at `http://localhost:5173`*

### 3. Android App (The Storefront)
1. Open the `app/` folder in **Android Studio**.
2. Place `google-services.json` in `app/`.
3. Press **Run** (Shift + F10).

---

## 🏗️ System Architecture

Our system is built for scale using a modern microservices approach:

```mermaid
graph TD
    A[Android App] -->|HTTP/JSON| B[API Gateway :8080]
    C[React Admin] -->|HTTP/JSON| B
    
    subgraph "Backend (Docker)"
    B --> D[Product Service :8081]
    B --> E[Order Service :8082]
    B --> F[Notification Service :8083]
    B --> G[Transaction Service :8084]
    end
    
    D & E & F & G --> H[(Firebase Firestore)]
    G --> I[Ethereum/BSC Blockchain]
```

---

## 📱 Android App Setup
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Key Config**: `app/google-services.json`
- **API Connection**: Edit `ApiClient.kt` -> `BASE_URL`.
  - Use `http://10.0.2.2:8080` for Emulator.
  - Use your Computer IP for physical devices.

---

## ⚙️ Backend Services
We use **Docker** to make setup effortless. Each service handles a specific part of the business:

| Service | Port | Responsibility |
| :--- | :--- | :--- |
| **API Gateway** | `8080` | Security, Routing, Token Verification |
| **Product Service** | `9091` | Catalog, Inventory, Categories |
| **Order Service** | `8092` | Checkout, Order History, Admin Stats |
| **Notification Service**| `9093` | Push Notifications, Alerts |
| **Transaction Service** | `9094` | Blockchain Verification, Replay Protection |

### 🔐 Firebase Setup
1. Go to Firebase Console -> Project Settings -> Service Accounts.
2. Generate a new private key.
3. Rename it to `firebase-service-account.json`.
4. Place it in `services/order-service/`, `services/api-gateway/`, etc. (or use the root `services/` folder if using the start script).

---

## 💻 Admin Dashboard
A high-performance dashboard built with **React + Vite + Lucide Icons**.

- **Features**:
  - 📊 **Real-time Stats**: Revenue, Orders, and Active Users.
  - 📦 **Product Management**: Add, Edit, and Delete products with ease.
  - 👥 **Admin Management**: Control who has access to the dashboard.
  - 🌙 **Dark Mode**: Premium glassmorphism design.

---

## 🔗 Blockchain Integration (Web3)
 Clot supports **CToken (CLOT)** payments on the BSC Testnet.
- **Network**: BSC Testnet (Chain ID 97)
- **Wallet**: MetaMask Integration
- **Security**: Server-side verification of every transaction hash to prevent double-spending.

---

## 🔍 Troubleshooting

**Q: Dashboard shows $0.00?**
- Check if `services-order-service` is running.
- Ensure your Firebase `orders` collection isn't empty.

**Q: Android App can't connect?**
- Make sure you aren't using `localhost`. Use `10.0.2.2` for the emulator.
- Check if the API Gateway container is up (`docker ps`).

**Q: "Port already allocated" error?**
- Run `docker compose down` to clear old containers, then try again.

---

**Happy Coding! 🚀**

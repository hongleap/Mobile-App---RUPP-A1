# Web Admin Dashboard (React)

This is a modern, responsive admin dashboard for managing the e-commerce store. It is built with React, Vite, and Lucide Icons, and connects to the Node.js microservices backend.

## Features
- **Admin Authentication**: Secure login using Firebase Auth.
- **Dashboard Overview**: Key metrics and statistics.
- **Product Management**: Full CRUD operations for products.
- **Responsive Design**: Works on desktop and tablets.
- **Premium UI**: Glassmorphism and smooth animations.

## Setup

1.  **Install Dependencies**:
    ```bash
    npm install
    ```

2.  **Configure Environment**:
    - Copy the example environment file:
      ```bash
      cp .env.example .env
      ```
    - Open `.env` and fill in your Firebase project credentials from the Firebase Console (Project Settings > General > Your apps > Web app).
    - Ensure `VITE_API_URL` points to your running backend (default: `http://localhost:8080`).

3.  **Run Development Server**:
    ```bash
    npm run dev
    ```

4.  **Build for Production**:
    ```bash
    npm run build
    ```

## Architecture
- **Frontend**: React (Vite)
- **Backend**: Node.js API Gateway (Port 8080)
- **Database**: Firebase Firestore
- **Auth**: Firebase Authentication

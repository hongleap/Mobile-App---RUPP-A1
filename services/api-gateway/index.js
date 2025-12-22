const express = require('express');
const axios = require('axios');
const admin = require('firebase-admin');
const cors = require('cors');
const morgan = require('morgan');
const fs = require('fs');
require('dotenv').config();

const app = express();
const port = process.env.PORT || 8080;

// Initialize Firebase Admin SDK
const serviceAccountPath = './firebase-service-account.json';
if (fs.existsSync(serviceAccountPath)) {
    try {
        const serviceAccount = require(serviceAccountPath);
        admin.initializeApp({
            credential: admin.credential.cert(serviceAccount)
        });
        console.log('Firebase Admin SDK initialized successfully');
    } catch (error) {
        console.error('WARNING: Failed to initialize Firebase:', error.message);
    }
} else {
    console.warn('WARNING: firebase-service-account.json not found. Token verification will fail.');
}

app.use(cors());
app.use(express.json());
app.use(morgan('dev'));

// Service URLs
const productServiceUrl = process.env.PRODUCT_SERVICE_URL || 'http://localhost:8081';
const orderServiceUrl = process.env.ORDER_SERVICE_URL || 'http://localhost:8082';
const notificationServiceUrl = process.env.NOTIFICATION_SERVICE_URL || 'http://localhost:8083';
const transactionServiceUrl = process.env.TRANSACTION_SERVICE_URL || 'http://localhost:8084';

// Middleware to verify Firebase token
const verifyToken = async (req, res, next) => {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({ success: false, error: 'Unauthorized' });
    }

    const token = authHeader.split(' ')[1];
    try {
        const decodedToken = await admin.auth().verifyIdToken(token);
        req.userId = decodedToken.uid;
        next();
    } catch (error) {
        return res.status(401).json({ success: false, error: 'Unauthorized' });
    }
};

// Helper function for proxying requests
const proxyRequest = async (req, res, targetUrl, requiresAuth = false) => {
    try {
        const config = {
            method: req.method,
            url: targetUrl,
            data: req.body,
            headers: {
                ...req.headers,
                'host': new URL(targetUrl).host
            }
        };

        if (requiresAuth && req.userId) {
            config.headers['X-User-Id'] = req.userId;
        }

        // Remove Authorization header before forwarding to internal services
        delete config.headers['authorization'];

        const response = await axios(config);
        res.status(response.status).send(response.data);
    } catch (error) {
        if (error.response) {
            res.status(error.response.status).send(error.response.data);
        } else {
            console.error(`Error proxying to ${targetUrl}:`, error.message);
            res.status(503).json({ success: false, error: 'Service unavailable' });
        }
    }
};

// Product Service Routes
app.get('/api/products', (req, res) => proxyRequest(req, res, `${productServiceUrl}/api/products`));
app.get('/api/products/:id', (req, res) => proxyRequest(req, res, `${productServiceUrl}/api/products/${req.params.id}`));
app.get('/api/products/category/:category', (req, res) => proxyRequest(req, res, `${productServiceUrl}/api/products/category/${req.params.category}`));
app.post('/api/products', verifyToken, (req, res) => proxyRequest(req, res, `${productServiceUrl}/api/products`, true));
app.put('/api/products/:id', verifyToken, (req, res) => proxyRequest(req, res, `${productServiceUrl}/api/products/${req.params.id}`, true));
app.delete('/api/products/:id', verifyToken, (req, res) => proxyRequest(req, res, `${productServiceUrl}/api/products/${req.params.id}`, true));

// Order Service Routes
app.get('/api/orders', verifyToken, (req, res) => proxyRequest(req, res, `${orderServiceUrl}/api/orders`, true));
app.get('/api/orders/:id', verifyToken, (req, res) => proxyRequest(req, res, `${orderServiceUrl}/api/orders/${req.params.id}`, true));
app.post('/api/orders', verifyToken, (req, res) => proxyRequest(req, res, `${orderServiceUrl}/api/orders`, true));
app.put('/api/orders/:id/status', (req, res) => proxyRequest(req, res, `${orderServiceUrl}/api/orders/${req.params.id}/status`));
app.get('/api/admin/stats', verifyToken, (req, res) => proxyRequest(req, res, `${orderServiceUrl}/api/admin/stats`, true));

// Notification Service Routes
app.get('/api/notifications', verifyToken, (req, res) => proxyRequest(req, res, `${notificationServiceUrl}/api/notifications`, true));
app.post('/api/notifications', (req, res) => proxyRequest(req, res, `${notificationServiceUrl}/api/notifications`));
app.put('/api/notifications/:id/read', (req, res) => proxyRequest(req, res, `${notificationServiceUrl}/api/notifications/${req.params.id}/read`));
app.put('/api/notifications/mark-all-read', verifyToken, (req, res) => proxyRequest(req, res, `${notificationServiceUrl}/api/notifications/mark-all-read`, true));

// Transaction Service Routes
app.post('/api/transactions/mark-consumed', verifyToken, (req, res) => proxyRequest(req, res, `${transactionServiceUrl}/api/transactions/mark-consumed`, true));
app.get('/api/transactions/is-consumed/:txHash', verifyToken, (req, res) => proxyRequest(req, res, `${transactionServiceUrl}/api/transactions/is-consumed/${req.params.txHash}`, true));
app.get('/api/transactions/user-transactions', verifyToken, (req, res) => proxyRequest(req, res, `${transactionServiceUrl}/api/transactions/user-transactions`, true));
app.post('/api/transactions/save', verifyToken, (req, res) => proxyRequest(req, res, `${transactionServiceUrl}/api/transactions/save`, true));
app.get('/api/transactions/history', verifyToken, (req, res) => proxyRequest(req, res, `${transactionServiceUrl}/api/transactions/history`, true));

app.listen(port, '0.0.0.0', () => {
    console.log(`API Gateway listening at http://0.0.0.0:${port}`);
});

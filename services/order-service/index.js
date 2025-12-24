const express = require('express');
const axios = require('axios');
const admin = require('firebase-admin');
const cors = require('cors');
const morgan = require('morgan');
const fs = require('fs');
require('dotenv').config();

const app = express();
const port = process.env.PORT || 8082;

// Initialize Firebase Admin SDK
const serviceAccountPath = './firebase-service-account.json';
let db;

if (fs.existsSync(serviceAccountPath)) {
    try {
        const serviceAccount = require(serviceAccountPath);

        // Fix private key formatting if it contains literal \n
        if (serviceAccount.private_key && serviceAccount.private_key.includes('\\n')) {
            serviceAccount.private_key = serviceAccount.private_key.replace(/\\n/g, '\n');
        }

        admin.initializeApp({
            credential: admin.credential.cert(serviceAccount)
        });
        db = admin.firestore();
        console.log('Firebase Admin SDK initialized successfully');
    } catch (error) {
        console.error('WARNING: Failed to initialize Firebase:', error.message);
    }
} else {
    console.warn('WARNING: firebase-service-account.json not found. Firebase operations will fail.');
}



app.use(cors());
app.use(express.json());
app.use(morgan('dev'));

const productServiceUrl = process.env.PRODUCT_SERVICE_URL || 'http://product-service:8081';
const notificationServiceUrl = process.env.NOTIFICATION_SERVICE_URL || 'http://notification-service:8083';

app.get('/', (req, res) => {
    res.json({
        service: 'Order Service',
        version: '1.0.0',
        status: 'running'
    });
});

// Get orders for a user
app.get('/api/orders', async (req, res) => {
    const userId = req.headers['x-user-id'];
    if (!userId) {
        return res.status(401).json({ success: false, error: 'User ID is required' });
    }

    try {
        const snapshot = await db.collection('orders')
            .where('userId', '==', userId)
            .get();

        const orders = snapshot.docs.map(doc => ({
            id: doc.id,
            ...doc.data()
        })).sort((a, b) => b.createdAt - a.createdAt);

        res.json({ success: true, data: orders });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Get order by ID
app.get('/api/orders/:id', async (req, res) => {
    const userId = req.headers['x-user-id'];
    if (!userId) {
        return res.status(401).json({ success: false, error: 'User ID is required' });
    }

    try {
        const doc = await db.collection('orders').doc(req.params.id).get();
        if (!doc.exists) {
            return res.status(404).json({ success: false, error: 'Order not found' });
        }

        const data = doc.data();
        if (data.userId !== userId) {
            return res.status(403).json({ success: false, error: 'Access denied' });
        }

        res.json({ success: true, data: { id: doc.id, ...data } });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Create an order
app.post('/api/orders', async (req, res) => {
    const userId = req.headers['x-user-id'];
    if (!userId) {
        return res.status(401).json({ success: false, error: 'User ID is required' });
    }

    const { items, customerName, customerEmail, shippingAddress, shippingPhone } = req.body;
    if (!items || !items.length) {
        return res.status(400).json({ success: false, error: 'Items are required' });
    }

    try {
        const orderNumber = `ORD${Date.now().toString().slice(-8)}`;
        const total = items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
        const createdAt = Date.now();

        const orderData = {
            userId,
            orderNumber,
            itemCount: items.length,
            status: 'Processing',
            total,
            customerName,
            customerEmail,
            shippingAddress,
            shippingPhone,
            createdAt,
            items
        };

        const docRef = await db.collection('orders').add(orderData);

        // Decrease stock (async, don't block response)
        items.forEach(item => {
            axios.post(`${productServiceUrl}/api/stock/decrease`, {
                productId: item.productId,
                quantity: item.quantity
            }).catch(err => console.error(`Failed to decrease stock for ${item.productId}:`, err.message));
        });

        // Create notification (async, don't block response)
        axios.post(`${notificationServiceUrl}/api/notifications`, {
            userId,
            message: `${customerName}, you placed an order. Check your order history for full details.`,
            type: 'order'
        }).catch(err => console.error(`Failed to create notification:`, err.message));

        res.status(201).json({
            success: true,
            data: { id: docRef.id, ...orderData }
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Update order status
app.put('/api/orders/:id/status', async (req, res) => {
    const { status } = req.body;
    if (!status) {
        return res.status(400).json({ success: false, error: 'Status is required' });
    }

    try {
        await db.collection('orders').doc(req.params.id).update({ status });
        res.json({ success: true, message: 'Order status updated' });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Admin Stats
app.get('/api/admin/stats', async (req, res) => {
    try {
        console.log('Fetching admin stats from Firestore...');
        const snapshot = await db.collection('orders').get();
        console.log(`Snapshot size: ${snapshot.size}`);
        const orders = snapshot.docs.map(doc => doc.data());
        console.log(`Found ${orders.length} orders in database.`);

        const totalOrders = orders.length;
        const totalRevenue = orders.reduce((sum, order) => sum + (parseFloat(order.total) || 0), 0);
        const avgOrder = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        // Count unique users
        const uniqueUsers = new Set(orders.map(order => order.userId)).size;

        // Get recent activity (last 5 orders)
        const recentActivity = snapshot.docs
            .map(doc => ({ id: doc.id, ...doc.data() }))
            .sort((a, b) => (b.createdAt || 0) - (a.createdAt || 0))
            .slice(0, 5);

        console.log('Stats calculated:', { totalOrders, totalRevenue, uniqueUsers });

        res.json({
            success: true,
            data: {
                totalRevenue,
                totalOrders,
                activeUsers: uniqueUsers,
                avgOrder,
                recentActivity
            }
        });
    } catch (error) {
        console.error('Error in admin stats:', error);
        res.status(500).json({ success: false, error: error.message });
    }
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Order Service listening at http://0.0.0.0:${port}`);
});

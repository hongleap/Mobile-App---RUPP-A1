const express = require('express');
const axios = require('axios');
const mongoose = require('mongoose');
const cors = require('cors');
const morgan = require('morgan');
require('dotenv').config();

const Order = require('./models/Order');

const app = express();
const port = process.env.PORT || 8082;

// MongoDB Connection
const mongodbUri = process.env.MONGODB_URI;
if (mongodbUri) {
    mongoose.connect(mongodbUri)
        .then(() => console.log('Connected to MongoDB Atlas'))
        .catch(err => console.error('MongoDB connection error:', err));
} else {
    console.warn('WARNING: MONGODB_URI not found. Database operations will fail.');
}

app.use(cors());
app.use(express.json());
app.use(morgan('dev'));

const productServiceUrl = process.env.PRODUCT_SERVICE_URL || 'http://product-service:8081';
const notificationServiceUrl = process.env.NOTIFICATION_SERVICE_URL || 'http://notification-service:8083';

app.get('/', (req, res) => {
    res.json({
        service: 'Order Service',
        version: '1.1.0',
        status: 'running',
        database: 'mongodb'
    });
});

// Get orders for a user
app.get('/api/orders', async (req, res) => {
    const userId = req.headers['x-user-id'];
    if (!userId) {
        return res.status(401).json({ success: false, error: 'User ID is required' });
    }

    try {
        const orders = await Order.find({ userId }).sort({ createdAt: -1 });
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
        const order = await Order.findById(req.params.id);
        if (!order) {
            return res.status(404).json({ success: false, error: 'Order not found' });
        }

        if (order.userId !== userId) {
            return res.status(403).json({ success: false, error: 'Access denied' });
        }

        res.json({ success: true, data: order });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Create an order
app.post('/api/orders', async (req, res) => {
    console.log(`[${new Date().toISOString()}] Received order creation request`);
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

        const order = new Order({
            userId,
            orderNumber,
            itemCount: items.length,
            status: 'Processing',
            total,
            customerName,
            customerEmail,
            shippingAddress,
            shippingPhone,
            items
        });

        console.log(`[${new Date().toISOString()}] Saving order ${orderNumber}...`);
        const saveStart = Date.now();
        await order.save();
        console.log(`[${new Date().toISOString()}] Order saved in ${Date.now() - saveStart}ms`);

        // Decrease stock (async, don't block response)
        setImmediate(() => {
            console.log(`[${new Date().toISOString()}] Triggering async stock decrease`);
            items.forEach(item => {
                axios.post(`${productServiceUrl}/api/stock/decrease`, {
                    productId: item.productId,
                    quantity: item.quantity
                }, { timeout: 5000 }).catch(err => console.error(`Failed to decrease stock for ${item.productId}:`, err.message));
            });
        });

        // Create notification (async, don't block response)
        setImmediate(() => {
            console.log(`[${new Date().toISOString()}] Triggering async notification`);
            axios.post(`${notificationServiceUrl}/api/notifications`, {
                userId,
                message: `${customerName}, you placed an order. Check your order history for full details.`,
                type: 'order'
            }, { timeout: 5000 }).catch(err => console.error(`Failed to create notification:`, err.message));
        });

        console.log(`[${new Date().toISOString()}] Sending response`);
        res.status(201).json({ success: true, data: order });
    } catch (error) {
        console.error(`[${new Date().toISOString()}] Error creating order:`, error);
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
        const order = await Order.findByIdAndUpdate(req.params.id, { status }, { new: true });
        if (!order) {
            return res.status(404).json({ success: false, error: 'Order not found' });
        }
        res.json({ success: true, message: 'Order status updated', data: order });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Admin Stats
app.get('/api/admin/stats', async (req, res) => {
    try {
        const stats = await Order.aggregate([
            {
                $group: {
                    _id: null,
                    totalRevenue: { $sum: "$total" },
                    totalOrders: { $sum: 1 },
                    uniqueUsers: { $addToSet: "$userId" }
                }
            }
        ]);

        const result = stats[0] || { totalRevenue: 0, totalOrders: 0, uniqueUsers: [] };
        const totalOrders = result.totalOrders;
        const totalRevenue = result.totalRevenue;
        const activeUsers = result.uniqueUsers.length;
        const avgOrder = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        const recentActivity = await Order.find().sort({ createdAt: -1 }).limit(5);

        res.json({
            success: true,
            data: {
                totalRevenue,
                totalOrders,
                activeUsers,
                avgOrder,
                recentActivity
            }
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Order Service listening at http://0.0.0.0:${port}`);
});

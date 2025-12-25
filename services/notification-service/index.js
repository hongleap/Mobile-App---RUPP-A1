const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const morgan = require('morgan');
require('dotenv').config();

const Notification = require('./models/Notification');

const app = express();
const port = process.env.PORT || 8083;

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

app.get('/', (req, res) => {
    res.json({
        service: 'Notification Service',
        version: '1.1.0',
        status: 'running',
        database: 'mongodb'
    });
});

// Get notifications for a user
app.get('/api/notifications', async (req, res) => {
    const userId = req.headers['x-user-id'];
    if (!userId) {
        return res.status(401).json({ success: false, error: 'User ID is required' });
    }

    try {
        const notifications = await Notification.find({ userId }).sort({ createdAt: -1 });
        res.json({ success: true, data: notifications });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Create a notification
app.post('/api/notifications', async (req, res) => {
    const { userId, message, type } = req.body;
    if (!userId || !message) {
        return res.status(400).json({ success: false, error: 'User ID and message are required' });
    }

    try {
        const notification = new Notification({
            userId,
            message,
            type: type || 'order'
        });
        await notification.save();
        res.status(201).json({ success: true, data: notification });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Mark notification as read
app.put('/api/notifications/:id/read', async (req, res) => {
    try {
        const notification = await Notification.findByIdAndUpdate(req.params.id, { isRead: true }, { new: true });
        if (!notification) {
            return res.status(404).json({ success: false, error: 'Notification not found' });
        }
        res.json({ success: true, message: 'Notification marked as read', data: notification });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Mark all notifications as read for a user
app.put('/api/notifications/mark-all-read', async (req, res) => {
    const userId = req.headers['x-user-id'];
    if (!userId) {
        return res.status(401).json({ success: false, error: 'User ID is required' });
    }

    try {
        await Notification.updateMany({ userId, isRead: false }, { isRead: true });
        res.json({ success: true, message: 'All notifications marked as read' });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Notification Service listening at http://0.0.0.0:${port}`);
});

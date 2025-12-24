const express = require('express');
const admin = require('firebase-admin');
const cors = require('cors');
const morgan = require('morgan');
const fs = require('fs');
require('dotenv').config();

const app = express();
const port = process.env.PORT || 8083;

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

app.get('/', (req, res) => {
    res.json({
        service: 'Notification Service',
        version: '1.0.0',
        status: 'running'
    });
});

// Get notifications for a user
app.get('/api/notifications', async (req, res) => {
    const userId = req.headers['x-user-id'];
    if (!userId) {
        return res.status(401).json({ success: false, error: 'User ID is required' });
    }

    try {
        const snapshot = await db.collection('notifications')
            .where('userId', '==', userId)
            .orderBy('createdAt', 'desc')
            .get();

        const notifications = snapshot.docs.map(doc => ({
            id: doc.id,
            ...doc.data()
        }));

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
        const notificationData = {
            userId,
            message,
            isRead: false,
            type: type || 'order',
            createdAt: Date.now()
        };

        const docRef = await db.collection('notifications').add(notificationData);
        res.status(201).json({
            success: true,
            data: { id: docRef.id, ...notificationData }
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Mark notification as read
app.put('/api/notifications/:id/read', async (req, res) => {
    try {
        await db.collection('notifications').doc(req.params.id).update({ isRead: true });
        res.json({ success: true, message: 'Notification marked as read' });
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
        const snapshot = await db.collection('notifications')
            .where('userId', '==', userId)
            .where('isRead', '==', false)
            .get();

        const batch = db.batch();
        snapshot.docs.forEach(doc => {
            batch.update(doc.ref, { isRead: true });
        });
        await batch.commit();

        res.json({ success: true, message: 'All notifications marked as read' });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Notification Service listening at http://0.0.0.0:${port}`);
});

const express = require('express');
const admin = require('firebase-admin');
const cors = require('cors');
const morgan = require('morgan');
const fs = require('fs');
require('dotenv').config();

const app = express();
const port = process.env.PORT || 8084;

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
    console.warn('WARNING: firebase-service-account.json not found. Firebase operations will fail.');
}

const db = admin.firestore();

app.use(cors());
app.use(express.json());
app.use(morgan('dev'));

app.get('/', (req, res) => {
    res.json({
        service: 'Transaction Service',
        version: '1.0.0',
        status: 'running'
    });
});

// Mark a transaction as consumed
app.post('/api/transactions/mark-consumed', async (req, res) => {
    const userId = req.headers['x-user-id'];
    if (!userId) {
        return res.status(401).json({ success: false, error: 'User ID is required' });
    }

    const { transactionHash, amount, timestamp } = req.body;
    if (!transactionHash) {
        return res.status(400).json({ success: false, error: 'Transaction hash is required' });
    }

    try {
        const transactionData = {
            transactionHash,
            userId,
            amount: amount || '0',
            timestamp: timestamp || Date.now(),
            createdAt: Date.now()
        };

        await db.collection('consumed_transactions').doc(transactionHash).set(transactionData);
        res.json({ success: true, message: 'Transaction marked as consumed' });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Check if a transaction is consumed
app.get('/api/transactions/is-consumed/:txHash', async (req, res) => {
    try {
        const doc = await db.collection('consumed_transactions').doc(req.params.txHash).get();
        res.json({ success: true, data: { consumed: doc.exists } });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Get all consumed transactions for a user
app.get('/api/transactions/user-transactions', async (req, res) => {
    const userId = req.headers['x-user-id'];
    if (!userId) {
        return res.status(401).json({ success: false, error: 'User ID is required' });
    }

    try {
        const snapshot = await db.collection('consumed_transactions')
            .where('userId', '==', userId)
            .get();

        const transactions = snapshot.docs.map(doc => ({
            transactionHash: doc.id,
            ...doc.data()
        }));

        res.json({ success: true, data: transactions });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Save a transaction to history
app.post('/api/transactions/save', async (req, res) => {
    const userId = req.headers['x-user-id'];
    if (!userId) {
        return res.status(401).json({ success: false, error: 'User ID is required' });
    }

    const { transactionHash, type, amount, fromAddress, toAddress, timestamp, status } = req.body;
    if (!transactionHash) {
        return res.status(400).json({ success: false, error: 'Transaction hash is required' });
    }

    try {
        const transactionData = {
            userId,
            transactionHash,
            type,
            amount,
            fromAddress,
            toAddress,
            timestamp,
            status,
            createdAt: Date.now()
        };

        await db.collection('transactions').add(transactionData);
        res.json({ success: true, message: 'Transaction saved' });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Get transaction history for user
app.get('/api/transactions/history', async (req, res) => {
    const userId = req.headers['x-user-id'];
    if (!userId) {
        return res.status(401).json({ success: false, error: 'User ID is required' });
    }

    try {
        const snapshot = await db.collection('transactions')
            .where('userId', '==', userId)
            .orderBy('timestamp', 'desc')
            .limit(100)
            .get();

        const transactions = snapshot.docs.map(doc => ({
            id: doc.id,
            ...doc.data()
        }));

        res.json({ success: true, data: transactions });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Transaction Service listening at http://0.0.0.0:${port}`);
});

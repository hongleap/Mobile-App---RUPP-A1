const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const morgan = require('morgan');
require('dotenv').config();

const Transaction = require('./models/Transaction');
const ConsumedTransaction = require('./models/ConsumedTransaction');

const app = express();
const port = process.env.PORT || 8084;

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
        service: 'Transaction Service',
        version: '1.1.0',
        status: 'running',
        database: 'mongodb'
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
        const consumedTx = new ConsumedTransaction({
            transactionHash,
            userId,
            amount: amount || '0',
            timestamp: timestamp || Date.now()
        });

        await consumedTx.save();
        res.json({ success: true, message: 'Transaction marked as consumed' });
    } catch (error) {
        if (error.code === 11000) {
            return res.status(400).json({ success: false, error: 'Transaction already consumed' });
        }
        res.status(500).json({ success: false, error: error.message });
    }
});

// Check if a transaction is consumed
app.get('/api/transactions/is-consumed/:txHash', async (req, res) => {
    try {
        const consumed = await ConsumedTransaction.exists({ transactionHash: req.params.txHash });
        res.json({ success: true, data: { consumed: !!consumed } });
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
        const transactions = await ConsumedTransaction.find({ userId });
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
        const transaction = new Transaction({
            userId,
            transactionHash,
            type,
            amount,
            fromAddress,
            toAddress,
            timestamp,
            status
        });

        await transaction.save();
        res.json({ success: true, message: 'Transaction saved' });
    } catch (error) {
        if (error.code === 11000) {
            return res.status(400).json({ success: false, error: 'Transaction already exists' });
        }
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
        const transactions = await Transaction.find({ userId })
            .sort({ timestamp: -1 })
            .limit(100);

        res.json({ success: true, data: transactions });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Transaction Service listening at http://0.0.0.0:${port}`);
});

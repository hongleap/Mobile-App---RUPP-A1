const express = require('express');
const admin = require('firebase-admin');
const cors = require('cors');
const morgan = require('morgan');
const fs = require('fs');
require('dotenv').config();

const app = express();
const port = process.env.PORT || 8081;

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

// Helper to format product
const formatProduct = (doc) => {
    const data = doc.data();
    return {
        id: doc.id,
        name: data.name || '',
        price: data.priceString || (data.price ? `$${data.price.toFixed(2)}` : '$0.00'),
        images: data.images || [],
        description: data.description || '',
        sizes: data.sizes || ['S', 'M', 'L', 'XL', '2XL'],
        colors: data.colors || [],
        category: data.category || '',
        gender: data.gender || '',
        onSale: data.onSale || false,
        freeShipping: data.freeShipping || false,
        stock: data.stock || 0
    };
};

app.get('/', (req, res) => {
    res.json({
        service: 'Product Service',
        version: '1.0.0',
        status: 'running'
    });
});

// Get all products
app.get('/api/products', async (req, res) => {
    try {
        const snapshot = await db.collection('products').get();
        const products = snapshot.docs.map(formatProduct);
        res.json({ success: true, data: products });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Get product by ID
app.get('/api/products/:id', async (req, res) => {
    try {
        const doc = await db.collection('products').document(req.params.id).get();
        if (!doc.exists) {
            return res.status(404).json({ success: false, error: 'Product not found' });
        }
        res.json({ success: true, data: formatProduct(doc) });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Get products by category
app.get('/api/products/category/:category', async (req, res) => {
    try {
        const snapshot = await db.collection('products')
            .where('category', '==', req.params.category)
            .get();
        const products = snapshot.docs.map(formatProduct);
        res.json({ success: true, data: products });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Create product
app.post('/api/products', async (req, res) => {
    try {
        const productData = {
            ...req.body,
            createdAt: admin.firestore.FieldValue.serverTimestamp()
        };
        const docRef = await db.collection('products').add(productData);
        res.status(201).json({ success: true, data: { id: docRef.id, ...productData } });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Update product
app.put('/api/products/:id', async (req, res) => {
    try {
        await db.collection('products').doc(req.params.id).update(req.body);
        res.json({ success: true, message: 'Product updated' });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Delete product
app.delete('/api/products/:id', async (req, res) => {
    try {
        await db.collection('products').doc(req.params.id).delete();
        res.json({ success: true, message: 'Product deleted' });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Decrease stock
app.post('/api/stock/decrease', async (req, res) => {
    const { productId, quantity } = req.body;
    if (!productId || quantity === undefined) {
        return res.status(400).json({ success: false, error: 'Product ID and quantity are required' });
    }

    try {
        const productRef = db.collection('products').doc(productId);
        const newStock = await db.runTransaction(async (t) => {
            const doc = await t.get(productRef);
            if (!doc.exists) {
                throw new Error('Product not found');
            }
            const currentStock = doc.data().stock || 0;
            const updatedStock = Math.max(0, currentStock - quantity);
            t.update(productRef, { stock: updatedStock });
            return updatedStock;
        });

        res.json({ success: true, data: { newStock } });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Product Service listening at http://0.0.0.0:${port}`);
});

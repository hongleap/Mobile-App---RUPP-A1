const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const morgan = require('morgan');
require('dotenv').config();

const Product = require('./models/Product');

const app = express();
const port = process.env.PORT || 8081;

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
        service: 'Product Service',
        version: '1.1.0',
        status: 'running',
        database: 'mongodb'
    });
});

// Get all products
app.get('/api/products', async (req, res) => {
    try {
        const includeHidden = req.query.includeHidden === 'true';
        const sort = req.query.sort;
        const limit = parseInt(req.query.limit) || 0;

        const query = includeHidden ? {} : { isHidden: { $ne: true } };

        let sortQuery = { createdAt: -1 };
        if (sort === 'sales') {
            sortQuery = { salesCount: -1 };
        } else if (sort === 'newest') {
            sortQuery = { createdAt: -1 };
        }

        const products = await Product.find(query).sort(sortQuery).limit(limit);
        res.json({ success: true, data: products });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Get product by ID
app.get('/api/products/:id', async (req, res) => {
    try {
        const product = await Product.findById(req.params.id);
        if (!product) {
            return res.status(404).json({ success: false, error: 'Product not found' });
        }
        res.json({ success: true, data: product });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Get products by category
app.get('/api/products/category/:category', async (req, res) => {
    try {
        const products = await Product.find({
            category: req.params.category,
            isHidden: { $ne: true }
        });
        res.json({ success: true, data: products });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Create product
app.post('/api/products', async (req, res) => {
    try {
        const product = new Product(req.body);
        await product.save();
        res.status(201).json({ success: true, data: product });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Update product
app.put('/api/products/:id', async (req, res) => {
    try {
        const product = await Product.findByIdAndUpdate(req.params.id, req.body, { new: true });
        if (!product) {
            return res.status(404).json({ success: false, error: 'Product not found' });
        }
        res.json({ success: true, message: 'Product updated', data: product });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Delete product
app.delete('/api/products/:id', async (req, res) => {
    try {
        const product = await Product.findByIdAndDelete(req.params.id);
        if (!product) {
            return res.status(404).json({ success: false, error: 'Product not found' });
        }
        res.json({ success: true, message: 'Product deleted' });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Decrease stock and increment sales
app.post('/api/stock/decrease', async (req, res) => {
    const { productId, quantity } = req.body;
    if (!productId || quantity === undefined) {
        return res.status(400).json({ success: false, error: 'Product ID and quantity are required' });
    }

    try {
        const product = await Product.findById(productId);
        if (!product) {
            return res.status(404).json({ success: false, error: 'Product not found' });
        }

        product.stock = Math.max(0, (product.stock || 0) - quantity);
        product.salesCount = (product.salesCount || 0) + quantity;
        await product.save();

        res.json({ success: true, data: { newStock: product.stock, salesCount: product.salesCount } });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// --- Banner Routes ---
const Banner = require('./models/Banner');

// Get active banner (for app)
app.get('/api/banners/active', async (req, res) => {
    try {
        const banners = await Banner.find({ isActive: true }).sort({ createdAt: -1 });
        res.json({ success: true, data: banners });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Get all banners (for admin)
app.get('/api/banners', async (req, res) => {
    try {
        const banners = await Banner.find().sort({ createdAt: -1 });
        res.json({ success: true, data: banners });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Create banner
app.post('/api/banners', async (req, res) => {
    try {
        // Removed logic that deactivates other banners
        const banner = new Banner(req.body);
        await banner.save();
        res.status(201).json({ success: true, data: banner });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Update banner
app.put('/api/banners/:id', async (req, res) => {
    try {
        // Removed logic that deactivates other banners
        const banner = await Banner.findByIdAndUpdate(req.params.id, req.body, { new: true });
        if (!banner) {
            return res.status(404).json({ success: false, error: 'Banner not found' });
        }
        res.json({ success: true, data: banner });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Delete banner
app.delete('/api/banners/:id', async (req, res) => {
    try {
        const banner = await Banner.findByIdAndDelete(req.params.id);
        if (!banner) {
            return res.status(404).json({ success: false, error: 'Banner not found' });
        }
        res.json({ success: true, message: 'Banner deleted' });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Product Service listening at http://0.0.0.0:${port}`);
});

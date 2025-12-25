const mongoose = require('mongoose');

const productSchema = new mongoose.Schema({
    _id: { type: String }, // Use Firestore ID
    name: { type: String, required: true },
    price: { type: Number, required: true },
    priceString: { type: String },
    images: [{ type: String }],
    description: { type: String },
    sizes: { type: [String], default: ['S', 'M', 'L', 'XL', '2XL'] },
    colors: [{
        name: { type: String },
        colorValue: { type: String }
    }],
    category: { type: String },
    gender: { type: String },
    onSale: { type: Boolean, default: false },
    freeShipping: { type: Boolean, default: false },
    stock: { type: Number, default: 0 },
    isHidden: { type: Boolean, default: false },
    salesCount: { type: Number, default: 0 },
    createdAt: { type: Date, default: Date.now }
});

// Virtual for id to match Firestore's doc.id
productSchema.virtual('id').get(function () {
    return this._id;
});

// Ensure virtuals are serialized
productSchema.set('toJSON', {
    virtuals: true,
    versionKey: false,
    transform: function (doc, ret) { delete ret._id }
});

module.exports = mongoose.model('Product', productSchema);

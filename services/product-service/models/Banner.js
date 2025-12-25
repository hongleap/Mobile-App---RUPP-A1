const mongoose = require('mongoose');

const bannerSchema = new mongoose.Schema({
    title: { type: String, required: true },
    subtitle: { type: String },
    discount: { type: String }, // e.g., "50% OFF"
    originalPrice: { type: String },
    imageUrl: { type: String },
    productId: { type: String }, // Optional link to a product
    isActive: { type: Boolean, default: true },
    createdAt: { type: Date, default: Date.now }
});

// Ensure virtuals are serialized
bannerSchema.set('toJSON', {
    virtuals: true,
    versionKey: false,
    transform: function (doc, ret) {
        ret.id = ret._id;
        delete ret._id;
        return ret;
    }
});

module.exports = mongoose.model('Banner', bannerSchema);

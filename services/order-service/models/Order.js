const mongoose = require('mongoose');

const orderItemSchema = new mongoose.Schema({
    productId: { type: String, required: true },
    productName: { type: String, required: true },
    price: { type: Number, required: true },
    quantity: { type: Number, required: true },
    imageUrl: { type: String },
    size: { type: String },
    color: { type: String },
    category: { type: String }
});

const orderSchema = new mongoose.Schema({
    _id: { type: String, default: () => new mongoose.Types.ObjectId().toString() }, // Use Firestore-like ID
    userId: { type: String, required: true, index: true },
    orderNumber: { type: String, required: true, unique: true },
    itemCount: { type: Number, required: true },
    status: { type: String, default: 'Processing' },
    total: { type: Number, required: true },
    customerName: { type: String },
    customerEmail: { type: String },
    shippingAddress: { type: String },
    shippingPhone: { type: String },
    items: [orderItemSchema],
    createdAt: { type: Date, default: Date.now }
});

// Virtual for id to match Firestore's doc.id
orderSchema.virtual('id').get(function () {
    return this._id;
});

// Ensure virtuals are serialized
orderSchema.set('toJSON', {
    virtuals: true,
    versionKey: false,
    transform: function (doc, ret) {
        delete ret._id;
        if (ret.createdAt instanceof Date) {
            ret.createdAt = ret.createdAt.getTime();
        }
        return ret;
    }
});

module.exports = mongoose.model('Order', orderSchema);

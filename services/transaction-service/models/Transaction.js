const mongoose = require('mongoose');

const transactionSchema = new mongoose.Schema({
    _id: { type: String, default: () => new mongoose.Types.ObjectId().toString() }, // Use Firestore-like ID
    userId: { type: String, required: true, index: true },
    transactionHash: { type: String, required: true, unique: true },
    type: { type: String },
    amount: { type: String },
    fromAddress: { type: String },
    toAddress: { type: String },
    timestamp: { type: Number, index: true },
    status: { type: String },
    createdAt: { type: Date, default: Date.now }
});

// Virtual for id to match Firestore's doc.id
transactionSchema.virtual('id').get(function () {
    return this._id;
});

// Ensure virtuals are serialized
transactionSchema.set('toJSON', {
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

module.exports = mongoose.model('Transaction', transactionSchema);

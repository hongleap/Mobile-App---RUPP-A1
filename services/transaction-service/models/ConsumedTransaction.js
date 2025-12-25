const mongoose = require('mongoose');

const consumedTransactionSchema = new mongoose.Schema({
    _id: { type: String }, // Use transactionHash as ID
    transactionHash: { type: String, required: true, unique: true },
    userId: { type: String, required: true, index: true },
    amount: { type: String },
    timestamp: { type: Number },
    createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('ConsumedTransaction', consumedTransactionSchema);

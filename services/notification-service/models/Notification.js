const mongoose = require('mongoose');

const notificationSchema = new mongoose.Schema({
    _id: { type: String, default: () => new mongoose.Types.ObjectId().toString() }, // Use Firestore-like ID
    userId: { type: String, required: true, index: true },
    message: { type: String, required: true },
    isRead: { type: Boolean, default: false },
    type: { type: String, default: 'order' },
    createdAt: { type: Date, default: Date.now }
});

// Virtual for id to match Firestore's doc.id
notificationSchema.virtual('id').get(function () {
    return this._id;
});

// Ensure virtuals are serialized
notificationSchema.set('toJSON', {
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

module.exports = mongoose.model('Notification', notificationSchema);

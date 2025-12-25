const { MongoClient } = require('mongodb');
const fs = require('fs');
const path = require('path');
require('dotenv').config();

async function syncImages() {
    const uri = process.env.MONGODB_URI;
    const gatewayUrl = 'http://192.168.100.48:8080';
    const imagesDir = path.join(__dirname, 'api-gateway', 'public', 'images');

    if (!uri) {
        console.error('MONGODB_URI not found in .env');
        process.exit(1);
    }

    const client = new MongoClient(uri);

    try {
        await client.connect();
        console.log('Connected to MongoDB');
        const db = client.db();
        const productsCollection = db.collection('products');

        const files = fs.readdirSync(imagesDir);
        const pngFiles = files.filter(file => file.toLowerCase().endsWith('.png'));

        console.log(`Found ${pngFiles.length} PNG images to sync.`);

        for (const file of pngFiles) {
            const productId = path.parse(file).name;
            const imageUrl = `${gatewayUrl}/images/${file}`;

            // Try to update the exact ID
            let result = await productsCollection.updateOne(
                { _id: productId },
                { $set: { images: [imageUrl] } }
            );

            if (result.matchedCount > 0) {
                console.log(`Updated product ${productId} with image ${imageUrl}`);
            }

            // If the ID has a leading zero (e.g., JA01), also try without it (e.g., JA1)
            if (productId.match(/[A-Z]{2}0[0-9]/)) {
                const altId = productId.replace(/([A-Z]{2})0([0-9])/, '$1$2');
                const altResult = await productsCollection.updateOne(
                    { _id: altId },
                    { $set: { images: [imageUrl] } }
                );
                if (altResult.matchedCount > 0) {
                    console.log(`Updated alternate ID ${altId} with image ${imageUrl}`);
                }
            }

            // If the ID doesn't have a leading zero (e.g., JA1), also try with it (e.g., JA01)
            if (productId.match(/[A-Z]{2}[1-9]$/)) {
                const altId = productId.replace(/([A-Z]{2})([1-9])/, '$10$2');
                const altResult = await productsCollection.updateOne(
                    { _id: altId },
                    { $set: { images: [imageUrl] } }
                );
                if (altResult.matchedCount > 0) {
                    console.log(`Updated alternate ID ${altId} with image ${imageUrl}`);
                }
            }
        }

        console.log('Sync completed successfully.');
    } catch (err) {
        console.error('Error syncing images:', err);
    } finally {
        await client.close();
    }
}

syncImages();

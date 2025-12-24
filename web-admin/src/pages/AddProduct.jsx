import React, { useState } from 'react';
import Sidebar from '../components/Sidebar';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Save, X, Image as ImageIcon } from 'lucide-react';
import { auth } from '../config/firebase';

const AddProduct = () => {
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        price: '',
        category: 'Hoodies',
        gender: 'Unisex',
        stock: '',
        imageUrl: '',
        onSale: false,
        freeShipping: false
    });
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const token = await auth.currentUser?.getIdToken();
            const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
            const response = await axios.post(`${apiUrl}/api/products`, {
                ...formData,
                price: parseFloat(formData.price),
                stock: parseInt(formData.stock),
                images: [formData.imageUrl],
                priceString: `$${parseFloat(formData.price).toFixed(2)}`
            }, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (response.data.success) {
                navigate('/products');
            }
        } catch (error) {
            console.error('Error adding product:', error);
            alert('Failed to add product. Check console for details.');
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
        setFormData({ ...formData, [e.target.name]: value });
    };

    return (
        <div style={{ display: 'flex' }}>
            <Sidebar />
            <main style={{ marginLeft: '260px', padding: '2rem', width: '100%' }}>
                <header style={{ marginBottom: '2.5rem' }}>
                    <h1 style={{ fontSize: '1.875rem', fontWeight: '700', marginBottom: '0.5rem' }}>Add New Product</h1>
                    <p style={{ color: 'var(--text-muted)' }}>Fill in the details below to add a new product to your catalog.</p>
                </header>

                <form onSubmit={handleSubmit} className="glass-card" style={{ maxWidth: '800px' }}>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', marginBottom: '1.5rem' }}>
                        <div style={{ gridColumn: 'span 2' }}>
                            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>Product Name</label>
                            <input
                                type="text"
                                name="name"
                                placeholder="e.g. Premium Oversized Hoodie"
                                style={{ width: '100%' }}
                                value={formData.name}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div style={{ gridColumn: 'span 2' }}>
                            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>Description</label>
                            <textarea
                                name="description"
                                placeholder="Describe your product..."
                                style={{ width: '100%', minHeight: '120px', background: 'var(--bg-dark)', border: '1px solid var(--border)', borderRadius: '8px', padding: '1rem', color: 'white' }}
                                value={formData.description}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div>
                            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>Price ($)</label>
                            <input
                                type="number"
                                name="price"
                                step="0.01"
                                placeholder="0.00"
                                style={{ width: '100%' }}
                                value={formData.price}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div>
                            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>Category</label>
                            <select
                                name="category"
                                style={{ width: '100%', background: 'var(--bg-dark)', border: '1px solid var(--border)', borderRadius: '8px', padding: '0.75rem 1rem', color: 'white' }}
                                value={formData.category}
                                onChange={handleChange}
                            >
                                <option value="Hoodies">Hoodies</option>
                                <option value="Jackets">Jackets</option>
                                <option value="T-Shirts">T-Shirts</option>
                                <option value="Pants">Pants</option>
                                <option value="Shoes">Shoes</option>
                                <option value="Accessories">Accessories</option>
                            </select>
                        </div>

                        <div>
                            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>Gender</label>
                            <select
                                name="gender"
                                style={{ width: '100%', background: 'var(--bg-dark)', border: '1px solid var(--border)', borderRadius: '8px', padding: '0.75rem 1rem', color: 'white' }}
                                value={formData.gender}
                                onChange={handleChange}
                            >
                                <option value="Unisex">Unisex</option>
                                <option value="Men">Men</option>
                                <option value="Women">Women</option>
                                <option value="Kids">Kids</option>
                            </select>
                        </div>

                        <div>
                            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>Initial Stock</label>
                            <input
                                type="number"
                                name="stock"
                                placeholder="0"
                                style={{ width: '100%' }}
                                value={formData.stock}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div style={{ gridColumn: 'span 2' }}>
                            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>Image URL</label>
                            <div style={{ position: 'relative' }}>
                                <ImageIcon style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={18} />
                                <input
                                    type="url"
                                    name="imageUrl"
                                    placeholder="https://example.com/image.jpg"
                                    style={{ width: '100%', paddingLeft: '3rem' }}
                                    value={formData.imageUrl}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                        </div>

                        <div style={{ display: 'flex', gap: '2rem', gridColumn: 'span 2', marginTop: '0.5rem' }}>
                            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
                                <input
                                    type="checkbox"
                                    name="onSale"
                                    checked={formData.onSale}
                                    onChange={handleChange}
                                    style={{ width: '18px', height: '18px' }}
                                />
                                <span style={{ fontSize: '0.875rem', fontWeight: '600' }}>On Sale</span>
                            </label>
                            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
                                <input
                                    type="checkbox"
                                    name="freeShipping"
                                    checked={formData.freeShipping}
                                    onChange={handleChange}
                                    style={{ width: '18px', height: '18px' }}
                                />
                                <span style={{ fontSize: '0.875rem', fontWeight: '600' }}>Free Shipping</span>
                            </label>
                        </div>
                    </div>

                    <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '1rem', marginTop: '2rem', paddingTop: '2rem', borderTop: '1px solid var(--border)' }}>
                        <button type="button" onClick={() => navigate('/products')} style={{ background: 'transparent', color: 'var(--text-muted)', fontWeight: '600', padding: '0.75rem 1.5rem' }}>
                            Cancel
                        </button>
                        <button type="submit" className="btn-primary" disabled={loading} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            {loading ? 'Saving...' : <><Save size={18} /> Save Product</>}
                        </button>
                    </div>
                </form>
            </main>
        </div>
    );
};

export default AddProduct;

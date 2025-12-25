
import React, { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import { Plus, Trash2, CheckCircle, Tag, X } from 'lucide-react';
import axios from 'axios';
import { auth } from '../config/firebase';

export default function Banners() {
    const [banners, setBanners] = useState([]);
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isCreating, setIsCreating] = useState(false);
    const [newBanner, setNewBanner] = useState({
        title: '',
        subtitle: '',
        discount: '',
        originalPrice: '',
        imageUrl: '',
        productId: '',
        isActive: true
    });

    const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

    useEffect(() => {
        fetchBanners();
        fetchProducts();
    }, []);

    const getAuthHeader = async () => {
        if (!auth.currentUser) return {};
        const token = await auth.currentUser.getIdToken();
        return { headers: { Authorization: `Bearer ${token}` } };
    };

    const fetchBanners = async () => {
        try {
            const config = await getAuthHeader();
            const res = await axios.get(`${API_URL}/api/banners`, config);
            if (res.data.success) {
                setBanners(res.data.data);
            }
        } catch (error) {
            console.error('Failed to fetch banners:', error);
        } finally {
            setLoading(false);
        }
    };

    const fetchProducts = async () => {
        try {
            const res = await axios.get(`${API_URL}/api/products`);
            if (res.data.success) {
                setProducts(res.data.data);
            }
        } catch (error) {
            console.error('Failed to fetch products:', error);
        }
    };

    const handleProductSelect = (productId) => {
        const product = products.find(p => p.id === productId);
        if (product) {
            setNewBanner({
                ...newBanner,
                title: product.name,
                subtitle: product.category,
                discount: product.price ? `$${product.price}` : '',
                originalPrice: product.price ? `$${product.price}` : '',
                imageUrl: product.images && product.images.length > 0 ? product.images[0] : '',
                productId: product.id
            });
        } else {
            setNewBanner({
                ...newBanner,
                productId: ''
            });
        }
    };

    const handleCreate = async (e) => {
        e.preventDefault();
        try {
            const config = await getAuthHeader();
            const res = await axios.post(`${API_URL}/api/banners`, newBanner, config);
            if (res.data.success) {
                setIsCreating(false);
                setNewBanner({ title: '', subtitle: '', discount: '', productId: '', isActive: true });
                fetchBanners();
            }
        } catch (error) {
            console.error('Failed to create banner:', error);
            alert('Failed to create banner. Please try again.');
        }
    };

    const handleDelete = async (id) => {
        if (!confirm('Are you sure you want to delete this banner?')) return;
        try {
            const config = await getAuthHeader();
            const res = await axios.delete(`${API_URL}/api/banners/${id}`, config);
            if (res.data.success) {
                fetchBanners();
            }
        } catch (error) {
            console.error('Failed to delete banner:', error);
            alert('Failed to delete banner.');
        }
    };

    const handleToggleActive = async (id, currentStatus) => {
        try {
            const config = await getAuthHeader();
            const res = await axios.put(`${API_URL}/api/banners/${id}`, { isActive: !currentStatus }, config);
            if (res.data.success) {
                fetchBanners();
            }
        } catch (error) {
            console.error('Failed to update banner:', error);
            alert('Failed to update banner status.');
        }
    };

    return (
        <div style={{ display: 'flex', background: 'var(--bg-dark)', minHeight: '100vh' }}>
            <Sidebar />
            <main style={{ marginLeft: '260px', padding: '3rem', width: '100%' }}>
                <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '3rem' }} className="animate-slide-up">
                    <div>
                        <h1 style={{ fontSize: '2.5rem', fontWeight: '800', marginBottom: '0.5rem', letterSpacing: '-0.02em' }}>Banners</h1>
                        <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>Manage promotional banners for the mobile app.</p>
                    </div>
                    <button
                        onClick={() => setIsCreating(true)}
                        className="btn-primary"
                        style={{ fontSize: '1.05rem' }}
                    >
                        <Plus size={20} />
                        New Banner
                    </button>
                </header>

                {isCreating && (
                    <div className="glass-card animate-scale-in" style={{ padding: '2rem', marginBottom: '3rem', border: '1px solid var(--primary-light)' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                            <h2 style={{ fontSize: '1.5rem', fontWeight: '700' }}>Create New Banner</h2>
                            <button onClick={() => setIsCreating(false)} style={{ background: 'transparent', color: 'var(--text-muted)' }}>
                                <X size={24} />
                            </button>
                        </div>
                        <form onSubmit={handleCreate} style={{ display: 'grid', gap: '1.5rem' }}>

                            <div>
                                <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)', fontSize: '0.9rem' }}>Select Product (Optional)</label>
                                <select
                                    value={newBanner.productId}
                                    onChange={(e) => handleProductSelect(e.target.value)}
                                    style={{ width: '100%', padding: '0.75rem', borderRadius: '12px', background: 'rgba(255, 255, 255, 0.03)', border: '1px solid var(--border)', color: 'white' }}
                                >
                                    <option value="">-- Choose a product to auto-fill --</option>
                                    {products.map(p => (
                                        <option key={p.id} value={p.id} style={{ color: 'black' }}>{p.name} - ${p.price}</option>
                                    ))}
                                </select>
                            </div>

                            {newBanner.productId && !newBanner.imageUrl && (
                                <div style={{ padding: '0.75rem', background: 'rgba(255, 165, 0, 0.1)', border: '1px solid orange', borderRadius: '8px', color: 'orange', fontSize: '0.9rem' }}>
                                    Warning: The selected product does not have an image. The banner will be displayed without a background image.
                                </div>
                            )}

                            <div>
                                <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)', fontSize: '0.9rem' }}>Image URL (Auto-filled)</label>
                                <input
                                    type="text"
                                    value={newBanner.imageUrl}
                                    readOnly
                                    placeholder="Product image URL will appear here"
                                    style={{ width: '100%', background: 'rgba(255, 255, 255, 0.05)', cursor: 'not-allowed' }}
                                />
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
                                <div>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)', fontSize: '0.9rem' }}>Title</label>
                                    <input
                                        type="text"
                                        required
                                        value={newBanner.title}
                                        onChange={e => setNewBanner({ ...newBanner, title: e.target.value })}
                                        placeholder="e.g., Summer Sale"
                                        style={{ width: '100%' }}
                                    />
                                </div>
                                <div>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)', fontSize: '0.9rem' }}>Discount / Price</label>
                                    <input
                                        type="text"
                                        value={newBanner.discount}
                                        onChange={e => setNewBanner({ ...newBanner, discount: e.target.value })}
                                        placeholder="e.g., 50% OFF"
                                        style={{ width: '100%' }}
                                    />
                                </div>
                            </div>
                            <div>
                                <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)', fontSize: '0.9rem' }}>Subtitle</label>
                                <input
                                    type="text"
                                    value={newBanner.subtitle}
                                    onChange={e => setNewBanner({ ...newBanner, subtitle: e.target.value })}
                                    placeholder="e.g., Limited time offer"
                                    style={{ width: '100%' }}
                                />
                            </div>

                            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', padding: '0.5rem 0' }}>
                                <input
                                    type="checkbox"
                                    id="isActive"
                                    checked={newBanner.isActive}
                                    onChange={e => setNewBanner({ ...newBanner, isActive: e.target.checked })}
                                    style={{ width: '20px', height: '20px', accentColor: 'var(--primary)' }}
                                />
                                <label htmlFor="isActive" style={{ cursor: 'pointer', fontWeight: '500' }}>Set as Active Banner</label>
                            </div>

                            <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
                                <button type="submit" className="btn-primary">
                                    Create Banner
                                </button>
                                <button
                                    type="button"
                                    onClick={() => setIsCreating(false)}
                                    style={{
                                        padding: '0.75rem 1.5rem',
                                        borderRadius: '12px',
                                        background: 'rgba(255,255,255,0.05)',
                                        color: 'var(--text-main)',
                                        fontWeight: '600'
                                    }}
                                >
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </div>
                )}

                {loading ? (
                    <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-muted)' }}>
                        <div className="animate-pulse" style={{ fontSize: '1.25rem' }}>Loading banners...</div>
                    </div>
                ) : (
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '2rem' }}>
                        {banners.map((banner, index) => (
                            <div
                                key={banner.id}
                                className="glass-card animate-slide-up"
                                style={{
                                    overflow: 'hidden',
                                    animationDelay: `${index * 100}ms`,
                                    border: banner.isActive ? '1px solid var(--primary)' : '1px solid var(--border)',
                                    boxShadow: banner.isActive ? '0 0 0 2px var(--primary-light)' : 'var(--shadow-premium)'
                                }}
                            >
                                <div style={{
                                    height: '140px',
                                    background: 'linear-gradient(135deg, var(--primary), #818cf8)',
                                    padding: '1.5rem',
                                    display: 'flex',
                                    flexDirection: 'column',
                                    justifyContent: 'center',
                                    color: 'white',
                                    position: 'relative'
                                }}>
                                    {banner.isActive && (
                                        <div style={{
                                            position: 'absolute',
                                            top: '1rem',
                                            right: '1rem',
                                            background: 'rgba(255,255,255,0.2)',
                                            backdropFilter: 'blur(4px)',
                                            padding: '0.25rem 0.75rem',
                                            borderRadius: '20px',
                                            fontSize: '0.75rem',
                                            fontWeight: '700',
                                            display: 'flex',
                                            alignItems: 'center',
                                            gap: '0.25rem'
                                        }}>
                                            <CheckCircle size={12} /> Active
                                        </div>
                                    )}
                                    <h3 style={{ fontSize: '1.25rem', fontWeight: '700', marginBottom: '0.25rem' }}>{banner.title}</h3>
                                    <p style={{ fontSize: '2rem', fontWeight: '900', letterSpacing: '-0.02em' }}>{banner.discount}</p>
                                </div>

                                <div style={{ padding: '1.5rem' }}>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.5rem', color: 'var(--text-muted)' }}>
                                        <Tag size={16} />
                                        <span style={{ fontSize: '0.9rem' }}>{banner.subtitle || 'No subtitle'}</span>
                                    </div>

                                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingTop: '1rem', borderTop: '1px solid var(--border)' }}>
                                        <button
                                            onClick={() => handleToggleActive(banner.id, banner.isActive)}
                                            style={{
                                                fontSize: '0.9rem',
                                                fontWeight: '600',
                                                color: banner.isActive ? 'var(--text-muted)' : 'var(--primary)',
                                                background: 'transparent',
                                                padding: '0.5rem',
                                                borderRadius: '8px'
                                            }}
                                        >
                                            {banner.isActive ? 'Deactivate' : 'Set Active'}
                                        </button>
                                        <button
                                            onClick={() => handleDelete(banner.id)}
                                            style={{
                                                color: 'var(--danger)',
                                                padding: '0.5rem',
                                                borderRadius: '8px',
                                                background: 'rgba(239, 68, 68, 0.1)'
                                            }}
                                        >
                                            <Trash2 size={18} />
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))}

                        {banners.length === 0 && !isCreating && (
                            <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '4rem', background: 'rgba(255,255,255,0.02)', borderRadius: '20px', border: '1px dashed var(--border)' }}>
                                <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>No banners found. Create one to get started!</p>
                            </div>
                        )}
                    </div>
                )}
            </main>
        </div>
    );
}


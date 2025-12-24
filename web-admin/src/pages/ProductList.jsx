import React, { useEffect, useState } from 'react';
import Sidebar from '../components/Sidebar';
import axios from 'axios';
import { Edit2, Trash2, Search, Plus, AlertCircle, RefreshCw, Package } from 'lucide-react';
import { Link } from 'react-router-dom';
import { auth } from '../config/firebase';

const ProductList = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');

    useEffect(() => {
        fetchProducts();
    }, []);

    const fetchProducts = async () => {
        setLoading(true);
        setError(null);
        try {
            const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
            const response = await axios.get(`${apiUrl}/api/products`);
            if (response.data && response.data.success) {
                setProducts(Array.isArray(response.data.data) ? response.data.data : []);
            } else {
                setError("Backend error: " + (response.data?.error || "Unknown"));
            }
        } catch (err) {
            console.error('Error fetching products:', err);
            setError("Connection error: " + err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (!id) return;
        if (!window.confirm('Are you sure you want to delete this product?')) return;
        try {
            const token = await auth.currentUser?.getIdToken();
            const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
            await axios.delete(`${apiUrl}/api/products/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setProducts(products.filter(p => p && p.id !== id));
        } catch (error) {
            console.error('Error deleting product:', error);
            alert('Failed to delete product: ' + (error.response?.data?.error || error.message));
        }
    };

    const filteredProducts = products.filter(p => {
        if (!p) return false;
        const name = String(p.name || '').toLowerCase();
        const category = String(p.category || '').toLowerCase();
        const search = searchTerm.toLowerCase();
        return name.includes(search) || category.includes(search);
    });

    // --- SAFETY HELPERS ---
    const getPrice = (p) => {
        if (!p) return '$0.00';
        // If backend already sent a formatted string (like "$29.99")
        if (typeof p.price === 'string' && p.price.includes('$')) return p.price;
        if (p.priceString) return String(p.priceString);

        // If it's a raw number
        const val = parseFloat(p.price);
        return isNaN(val) ? '$0.00' : `$${val.toFixed(2)}`;
    };

    const getImage = (p) => {
        if (Array.isArray(p.images) && p.images.length > 0) return String(p.images[0]);
        if (p.imageUrl) return String(p.imageUrl);
        return 'https://via.placeholder.com/40';
    };

    return (
        <div style={{ display: 'flex', background: 'var(--bg-dark)', minHeight: '100vh' }}>
            <Sidebar />
            <main style={{ marginLeft: '260px', padding: '3rem', width: '100%' }}>
                <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '3rem' }} className="animate-slide-up">
                    <div>
                        <h1 style={{ fontSize: '2.5rem', fontWeight: '800', marginBottom: '0.5rem', letterSpacing: '-0.02em' }}>Products</h1>
                        <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>Manage your {products.length} store items.</p>
                    </div>
                    <Link to="/add-product" className="btn-primary" style={{ textDecoration: 'none', fontSize: '1.05rem', padding: '0.85rem 1.75rem' }}>
                        <Plus size={22} /> Add New Product
                    </Link>
                </header>

                <div className="animate-slide-up" style={{ marginBottom: '3rem', position: 'relative', animationDelay: '100ms' }}>
                    <Search style={{ position: 'absolute', left: '1.5rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={22} />
                    <input
                        type="text"
                        placeholder="Search products by name or category..."
                        style={{
                            width: '100%',
                            padding: '1.25rem 1.25rem 1.25rem 4rem',
                            fontSize: '1.1rem',
                            background: 'var(--bg-card)',
                            border: '1px solid var(--border)',
                            borderRadius: '16px',
                            boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
                        }}
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>

                {error ? (
                    <div className="glass-card flex-center animate-scale-in" style={{ flexDirection: 'column', padding: '5rem', gap: '1.5rem' }}>
                        <div style={{ background: 'rgba(239, 68, 68, 0.1)', padding: '1.5rem', borderRadius: '24px' }}>
                            <AlertCircle size={48} color="var(--danger)" />
                        </div>
                        <h2 style={{ fontSize: '1.75rem', fontWeight: '800' }}>Oops! Something went wrong</h2>
                        <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem', maxWidth: '400px', textAlign: 'center' }}>{error}</p>
                        <button onClick={fetchProducts} className="btn-primary" style={{ fontSize: '1.1rem', marginTop: '1rem' }}>
                            <RefreshCw size={20} /> Try Again
                        </button>
                    </div>
                ) : (
                    <div className="glass-card animate-slide-up" style={{ padding: '0', overflow: 'hidden', animationDelay: '200ms' }}>
                        <div style={{ overflowX: 'auto' }}>
                            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                                <thead>
                                    <tr style={{ borderBottom: '1px solid var(--border)', background: 'rgba(255,255,255,0.02)' }}>
                                        <th style={{ padding: '1.5rem 2rem', color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.1em' }}>Product</th>
                                        <th style={{ padding: '1.5rem 2rem', color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.1em' }}>Category</th>
                                        <th style={{ padding: '1.5rem 2rem', color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.1em' }}>Price</th>
                                        <th style={{ padding: '1.5rem 2rem', color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.1em' }}>Stock</th>
                                        <th style={{ padding: '1.5rem 2rem', color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.1em' }}>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {loading ? (
                                        <tr>
                                            <td colSpan="5" style={{ padding: '6rem', textAlign: 'center' }}>
                                                <div className="animate-pulse" style={{ fontSize: '1.25rem', color: 'var(--text-muted)', fontWeight: '600' }}>
                                                    Loading products...
                                                </div>
                                            </td>
                                        </tr>
                                    ) : filteredProducts.length === 0 ? (
                                        <tr>
                                            <td colSpan="5" style={{ padding: '6rem', textAlign: 'center' }}>
                                                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1rem' }}>
                                                    <Package size={48} color="var(--text-muted)" style={{ opacity: 0.5 }} />
                                                    <div style={{ fontSize: '1.25rem', color: 'var(--text-muted)', fontWeight: '600' }}>
                                                        No products found.
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    ) : filteredProducts.map((p, i) => (
                                        <tr
                                            key={p?.id || i}
                                            style={{
                                                borderBottom: '1px solid var(--border)',
                                                transition: 'all 0.3s ease',
                                                background: 'transparent'
                                            }}
                                            onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
                                            onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                                        >
                                            <td style={{ padding: '1.25rem 2rem' }}>
                                                <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
                                                    <div style={{ position: 'relative' }}>
                                                        <img
                                                            src={getImage(p)}
                                                            alt=""
                                                            style={{ width: '60px', height: '60px', borderRadius: '14px', objectFit: 'cover', border: '1px solid var(--border)' }}
                                                            onError={(e) => e.target.src = 'https://via.placeholder.com/60'}
                                                        />
                                                    </div>
                                                    <span style={{ fontWeight: '700', fontSize: '1.1rem', color: 'var(--text-main)' }}>
                                                        {String(p?.name || 'Unnamed')}
                                                    </span>
                                                </div>
                                            </td>
                                            <td style={{ padding: '1.25rem 2rem' }}>
                                                <span style={{
                                                    background: 'var(--primary-light)',
                                                    color: 'var(--primary)',
                                                    padding: '0.5rem 1rem',
                                                    borderRadius: '10px',
                                                    fontSize: '0.85rem',
                                                    fontWeight: '700',
                                                    letterSpacing: '0.02em'
                                                }}>
                                                    {String(p?.category || 'N/A')}
                                                </span>
                                            </td>
                                            <td style={{ padding: '1.25rem 2rem', fontWeight: '800', fontSize: '1.15rem', color: 'var(--text-main)' }}>
                                                {getPrice(p)}
                                            </td>
                                            <td style={{ padding: '1.25rem 2rem' }}>
                                                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                                    <div style={{
                                                        width: '8px',
                                                        height: '8px',
                                                        borderRadius: '50%',
                                                        background: (p?.stock || 0) > 10 ? 'var(--accent)' : (p?.stock || 0) > 0 ? 'var(--warning)' : 'var(--danger)'
                                                    }} />
                                                    <span style={{ fontSize: '1.1rem', fontWeight: '600', color: 'var(--text-main)' }}>
                                                        {String(p?.stock || 0)}
                                                    </span>
                                                </div>
                                            </td>
                                            <td style={{ padding: '1.25rem 2rem' }}>
                                                <div style={{ display: 'flex', gap: '0.75rem' }}>
                                                    <button style={{
                                                        background: 'rgba(255,255,255,0.05)',
                                                        color: 'var(--text-muted)',
                                                        padding: '0.6rem',
                                                        borderRadius: '10px',
                                                        border: '1px solid var(--border)'
                                                    }} onMouseEnter={(e) => e.currentTarget.style.color = 'var(--text-main)'}
                                                        onMouseLeave={(e) => e.currentTarget.style.color = 'var(--text-muted)'}>
                                                        <Edit2 size={20} />
                                                    </button>
                                                    <button
                                                        onClick={() => handleDelete(p?.id)}
                                                        style={{
                                                            background: 'rgba(239, 68, 68, 0.05)',
                                                            color: 'var(--danger)',
                                                            padding: '0.6rem',
                                                            borderRadius: '10px',
                                                            border: '1px solid transparent'
                                                        }}
                                                        onMouseEnter={(e) => {
                                                            e.currentTarget.style.background = 'rgba(239, 68, 68, 0.1)';
                                                            e.currentTarget.style.borderColor = 'rgba(239, 68, 68, 0.2)';
                                                        }}
                                                        onMouseLeave={(e) => {
                                                            e.currentTarget.style.background = 'rgba(239, 68, 68, 0.05)';
                                                            e.currentTarget.style.borderColor = 'transparent';
                                                        }}
                                                    >
                                                        <Trash2 size={20} />
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}
            </main>
        </div>
    );
};

export default ProductList;

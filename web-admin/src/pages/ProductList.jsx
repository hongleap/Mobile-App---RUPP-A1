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
            const response = await axios.get('http://localhost:8080/api/products');
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
            await axios.delete(`http://localhost:8080/api/products/${id}`, {
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
        <div style={{ display: 'flex' }}>
            <Sidebar />
            <main style={{ marginLeft: '260px', padding: '2rem', width: '100%' }}>
                <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2.5rem' }}>
                    <div>
                        <h1 style={{ fontSize: '2.25rem', fontWeight: '800', marginBottom: '0.5rem' }}>Products</h1>
                        <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>Manage your {products.length} store items.</p>
                    </div>
                    <Link to="/add-product" className="btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', textDecoration: 'none', fontSize: '1.1rem', padding: '1rem 2rem' }}>
                        <Plus size={22} /> Add New Product
                    </Link>
                </header>

                <div style={{ marginBottom: '2.5rem', position: 'relative' }}>
                    <Search style={{ position: 'absolute', left: '1.25rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={24} />
                    <input
                        type="text"
                        placeholder="Search products..."
                        style={{ width: '100%', padding: '1.25rem 1.25rem 1.25rem 3.5rem', fontSize: '1.1rem' }}
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>

                {error ? (
                    <div className="glass-card flex-center" style={{ flexDirection: 'column', padding: '4rem', gap: '1.5rem' }}>
                        <AlertCircle size={64} color="var(--danger)" />
                        <h2 style={{ fontSize: '1.5rem' }}>Oops! Something went wrong</h2>
                        <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>{error}</p>
                        <button onClick={fetchProducts} className="btn-primary" style={{ fontSize: '1.1rem' }}>Try Again</button>
                    </div>
                ) : (
                    <div className="glass-card" style={{ padding: '0', overflow: 'hidden' }}>
                        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                            <thead>
                                <tr style={{ borderBottom: '1px solid var(--border)', background: 'rgba(255,255,255,0.02)' }}>
                                    <th style={{ padding: '1.5rem', color: 'var(--text-muted)', fontSize: '1rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Product</th>
                                    <th style={{ padding: '1.5rem', color: 'var(--text-muted)', fontSize: '1rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Category</th>
                                    <th style={{ padding: '1.5rem', color: 'var(--text-muted)', fontSize: '1rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Price</th>
                                    <th style={{ padding: '1.5rem', color: 'var(--text-muted)', fontSize: '1rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Stock</th>
                                    <th style={{ padding: '1.5rem', color: 'var(--text-muted)', fontSize: '1rem', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {loading ? (
                                    <tr><td colSpan="5" style={{ padding: '5rem', textAlign: 'center', fontSize: '1.25rem' }}>Loading...</td></tr>
                                ) : filteredProducts.length === 0 ? (
                                    <tr><td colSpan="5" style={{ padding: '5rem', textAlign: 'center', fontSize: '1.25rem' }}>No products found.</td></tr>
                                ) : filteredProducts.map((p, i) => (
                                    <tr key={p?.id || i} style={{ borderBottom: '1px solid var(--border)', transition: 'background 0.2s' }}>
                                        <td style={{ padding: '1.5rem' }}>
                                            <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
                                                <img
                                                    src={getImage(p)}
                                                    alt=""
                                                    style={{ width: '56px', height: '56px', borderRadius: '12px', objectFit: 'cover' }}
                                                    onError={(e) => e.target.src = 'https://via.placeholder.com/56'}
                                                />
                                                <span style={{ fontWeight: '700', fontSize: '1.1rem' }}>{String(p?.name || 'Unnamed')}</span>
                                            </div>
                                        </td>
                                        <td style={{ padding: '1.5rem' }}>
                                            <span style={{ background: 'rgba(99, 102, 241, 0.1)', color: 'var(--primary)', padding: '0.5rem 1rem', borderRadius: '20px', fontSize: '0.875rem', fontWeight: '700' }}>
                                                {String(p?.category || 'N/A')}
                                            </span>
                                        </td>
                                        <td style={{ padding: '1.5rem', fontWeight: '700', fontSize: '1.1rem', color: 'var(--text-main)' }}>{getPrice(p)}</td>
                                        <td style={{ padding: '1.5rem', fontSize: '1.1rem', fontWeight: '600' }}>{String(p?.stock || 0)}</td>
                                        <td style={{ padding: '1.5rem' }}>
                                            <div style={{ display: 'flex', gap: '1rem' }}>
                                                <button style={{ background: 'transparent', color: 'var(--text-muted)' }}><Edit2 size={22} /></button>
                                                <button onClick={() => handleDelete(p?.id)} style={{ background: 'transparent', color: 'var(--danger)' }}><Trash2 size={22} /></button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </main>
        </div>
    );
};

export default ProductList;

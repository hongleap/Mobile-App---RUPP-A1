import React, { useEffect, useState } from 'react';
import Sidebar from '../components/Sidebar';
import StatCard from '../components/StatCard';
import { ShoppingCart, Users, DollarSign, TrendingUp, Clock, Package } from 'lucide-react';
import axios from 'axios';
import { auth } from '../config/firebase';
import { onAuthStateChanged } from 'firebase/auth';

const Dashboard = () => {
    const [stats, setStats] = useState({
        totalRevenue: 0,
        totalOrders: 0,
        activeUsers: 0,
        avgOrder: 0,
        recentActivity: []
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                setLoading(true);
                const user = auth.currentUser;
                if (!user) return;

                const token = await user.getIdToken();
                const response = await axios.get('http://localhost:8080/api/admin/stats', {
                    headers: { Authorization: `Bearer ${token}` }
                });

                if (response.data.success) {
                    setStats(response.data.data);
                }
            } catch (error) {
                console.error('Dashboard: Error fetching stats:', error);
            } finally {
                setLoading(false);
            }
        };

        const unsubscribe = onAuthStateChanged(auth, (user) => {
            if (user) {
                fetchStats();
            }
        });

        return () => unsubscribe();
    }, []);

    return (
        <div style={{ display: 'flex', background: 'var(--bg-dark)', minHeight: '100vh' }}>
            <Sidebar />
            <main style={{ marginLeft: '260px', padding: '3rem', width: '100%' }}>
                <header style={{ marginBottom: '3rem' }} className="animate-slide-up">
                    <h1 style={{ fontSize: '2.5rem', fontWeight: '800', marginBottom: '0.5rem', letterSpacing: '-0.02em' }}>
                        Dashboard Overview
                    </h1>
                    <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>
                        Real-time statistics from your store database.
                    </p>
                </header>

                <div style={{ display: 'flex', gap: '1.5rem', flexWrap: 'wrap', marginBottom: '3rem' }}>
                    <StatCard
                        icon={<DollarSign size={24} />}
                        label="Total Revenue"
                        value={`$${stats.totalRevenue.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`}
                        color="#6366f1"
                        loading={loading}
                        delay={100}
                    />
                    <StatCard
                        icon={<ShoppingCart size={24} />}
                        label="Total Orders"
                        value={stats.totalOrders.toLocaleString()}
                        color="#10b981"
                        loading={loading}
                        delay={200}
                    />
                    <StatCard
                        icon={<Users size={24} />}
                        label="Active Users"
                        value={stats.activeUsers.toLocaleString()}
                        color="#f59e0b"
                        loading={loading}
                        delay={300}
                    />
                    <StatCard
                        icon={<TrendingUp size={24} />}
                        label="Avg. Order"
                        value={`$${stats.avgOrder.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`}
                        color="#ec4899"
                        loading={loading}
                        delay={400}
                    />
                </div>

                <div className="glass-card animate-slide-up" style={{ padding: '2.5rem', animationDelay: '500ms' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '2.5rem' }}>
                        <div style={{ background: 'var(--primary-light)', padding: '0.6rem', borderRadius: '10px' }}>
                            <Clock size={22} color="var(--primary)" />
                        </div>
                        <h2 style={{ fontSize: '1.5rem', fontWeight: '700', letterSpacing: '-0.01em' }}>Recent Orders</h2>
                    </div>

                    {loading ? (
                        <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-muted)' }}>
                            <div className="animate-pulse" style={{ fontSize: '1.1rem' }}>Loading activity...</div>
                        </div>
                    ) : stats.recentActivity.length === 0 ? (
                        <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-muted)', fontSize: '1.1rem' }}>
                            No recent orders found.
                        </div>
                    ) : (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
                            {stats.recentActivity.map((order, index) => (
                                <div
                                    key={order.id}
                                    className="animate-scale-in"
                                    style={{
                                        display: 'flex',
                                        justifyContent: 'space-between',
                                        alignItems: 'center',
                                        padding: '1.5rem',
                                        background: 'rgba(255,255,255,0.02)',
                                        borderRadius: '16px',
                                        border: '1px solid var(--border)',
                                        transition: 'all 0.3s ease',
                                        animationDelay: `${600 + (index * 100)}ms`
                                    }}
                                >
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
                                        <div style={{
                                            background: 'var(--primary-light)',
                                            padding: '0.8rem',
                                            borderRadius: '12px',
                                            color: 'var(--primary)'
                                        }}>
                                            <Package size={22} />
                                        </div>
                                        <div>
                                            <div style={{ fontWeight: '700', fontSize: '1.15rem', marginBottom: '0.25rem' }}>
                                                {order.customerName || 'Guest Customer'}
                                            </div>
                                            <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem', fontWeight: '500' }}>
                                                {order.orderNumber} • {new Date(order.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' })}
                                            </div>
                                        </div>
                                    </div>
                                    <div style={{ textAlign: 'right' }}>
                                        <div style={{ fontWeight: '800', fontSize: '1.25rem', color: 'var(--text-main)', marginBottom: '0.5rem' }}>
                                            +${parseFloat(order.total).toFixed(2)}
                                        </div>
                                        <div style={{
                                            fontSize: '0.75rem',
                                            fontWeight: '700',
                                            textTransform: 'uppercase',
                                            letterSpacing: '0.05em',
                                            color: order.status === 'Completed' ? 'var(--accent)' : 'var(--warning)',
                                            background: order.status === 'Completed' ? 'var(--accent-light)' : 'rgba(245, 158, 11, 0.1)',
                                            padding: '0.4rem 0.8rem',
                                            borderRadius: '8px',
                                            display: 'inline-block'
                                        }}>
                                            {order.status}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
};

export default Dashboard;

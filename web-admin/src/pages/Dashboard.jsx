import React, { useEffect, useState } from 'react';
import Sidebar from '../components/Sidebar';
import { ShoppingCart, Users, DollarSign, TrendingUp, Clock, Package } from 'lucide-react';
import axios from 'axios';
import { auth } from '../config/firebase';

const StatCard = ({ icon, label, value, color, loading }) => (
    <div className="glass-card" style={{ padding: '1.5rem', flex: 1, minWidth: '240px', transition: 'transform 0.3s ease' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1rem' }}>
            <div style={{ background: color, padding: '0.75rem', borderRadius: '12px', display: 'flex' }}>
                {icon}
            </div>
            <span style={{ color: 'var(--text-muted)', fontSize: '1rem', fontWeight: '600' }}>{label}</span>
        </div>
        <h3 style={{ fontSize: '2rem', fontWeight: '800' }}>
            {loading ? '...' : value}
        </h3>
    </div>
);

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
                const token = await auth.currentUser?.getIdToken();
                const response = await axios.get('http://localhost:8080/api/admin/stats', {
                    headers: { Authorization: `Bearer ${token}` }
                });
                if (response.data.success) {
                    setStats(response.data.data);
                }
            } catch (error) {
                console.error('Error fetching stats:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchStats();
    }, []);

    return (
        <div style={{ display: 'flex' }}>
            <Sidebar />
            <main style={{ marginLeft: '260px', padding: '2rem', width: '100%' }}>
                <header style={{ marginBottom: '2.5rem' }}>
                    <h1 style={{ fontSize: '2.25rem', fontWeight: '800', marginBottom: '0.5rem' }}>Dashboard Overview</h1>
                    <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>Real-time statistics from your store database.</p>
                </header>

                <div style={{ display: 'flex', gap: '1.5rem', flexWrap: 'wrap', marginBottom: '2.5rem' }}>
                    <StatCard
                        icon={<DollarSign color="white" size={24} />}
                        label="Total Revenue"
                        value={`$${stats.totalRevenue.toFixed(2)}`}
                        color="#6366f1"
                        loading={loading}
                    />
                    <StatCard
                        icon={<ShoppingCart color="white" size={24} />}
                        label="Total Orders"
                        value={stats.totalOrders.toString()}
                        color="#10b981"
                        loading={loading}
                    />
                    <StatCard
                        icon={<Users color="white" size={24} />}
                        label="Active Users"
                        value={stats.activeUsers.toString()}
                        color="#f59e0b"
                        loading={loading}
                    />
                    <StatCard
                        icon={<TrendingUp color="white" size={24} />}
                        label="Avg. Order"
                        value={`$${stats.avgOrder.toFixed(2)}`}
                        color="#ec4899"
                        loading={loading}
                    />
                </div>

                <div className="glass-card" style={{ padding: '2rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '2rem' }}>
                        <Clock size={24} color="var(--primary)" />
                        <h2 style={{ fontSize: '1.5rem', fontWeight: '700' }}>Recent Orders</h2>
                    </div>

                    {loading ? (
                        <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>Loading activity...</div>
                    ) : stats.recentActivity.length === 0 ? (
                        <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>No recent orders found.</div>
                    ) : (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            {stats.recentActivity.map((order) => (
                                <div key={order.id} style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                    padding: '1.25rem',
                                    background: 'rgba(255,255,255,0.03)',
                                    borderRadius: '12px',
                                    border: '1px solid var(--border)'
                                }}>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                                        <div style={{ background: 'rgba(99, 102, 241, 0.1)', padding: '0.75rem', borderRadius: '10px' }}>
                                            <Package size={20} color="var(--primary)" />
                                        </div>
                                        <div>
                                            <div style={{ fontWeight: '700', fontSize: '1.1rem' }}>{order.customerName || 'Guest Customer'}</div>
                                            <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>{order.orderNumber} • {new Date(order.createdAt).toLocaleDateString()}</div>
                                        </div>
                                    </div>
                                    <div style={{ textAlign: 'right' }}>
                                        <div style={{ fontWeight: '800', fontSize: '1.1rem', color: 'var(--accent)' }}>+${parseFloat(order.total).toFixed(2)}</div>
                                        <div style={{
                                            fontSize: '0.75rem',
                                            fontWeight: '700',
                                            textTransform: 'uppercase',
                                            color: order.status === 'Completed' ? 'var(--accent)' : 'var(--f59e0b)',
                                            background: order.status === 'Completed' ? 'rgba(16, 185, 129, 0.1)' : 'rgba(245, 158, 11, 0.1)',
                                            padding: '0.25rem 0.5rem',
                                            borderRadius: '4px',
                                            marginTop: '0.25rem',
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

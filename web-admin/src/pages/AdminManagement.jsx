import React, { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import { db } from '../config/firebase';
import { collection, getDocs, doc, setDoc, deleteDoc } from 'firebase/firestore';
import { UserPlus, Trash2, Shield } from 'lucide-react';

const AdminManagement = () => {
    const [admins, setAdmins] = useState([]);
    const [loading, setLoading] = useState(true);
    const [newAdminId, setNewAdminId] = useState('');
    const [newAdminEmail, setNewAdminEmail] = useState('');

    useEffect(() => {
        fetchAdmins();
    }, []);

    const fetchAdmins = async () => {
        try {
            const querySnapshot = await getDocs(collection(db, 'admins'));
            const adminList = querySnapshot.docs.map(doc => ({
                id: doc.id,
                ...doc.data()
            }));
            setAdmins(adminList);
        } catch (error) {
            console.error('Error fetching admins:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleAddAdmin = async (e) => {
        e.preventDefault();
        if (!newAdminId || !newAdminEmail) return;
        try {
            await setDoc(doc(db, 'admins', newAdminId), {
                email: newAdminEmail,
                role: 'admin',
                createdAt: new Date().toISOString()
            });
            setNewAdminId('');
            setNewAdminEmail('');
            fetchAdmins();
            alert('Admin added successfully!');
        } catch (error) {
            console.error('Error adding admin:', error);
            alert('Failed to add admin.');
        }
    };

    const handleDeleteAdmin = async (id) => {
        if (!window.confirm('Are you sure you want to remove this admin?')) return;
        try {
            await deleteDoc(doc(db, 'admins', id));
            fetchAdmins();
        } catch (error) {
            console.error('Error deleting admin:', error);
            alert('Failed to delete admin.');
        }
    };

    return (
        <div style={{ display: 'flex' }}>
            <Sidebar />
            <main style={{ marginLeft: '260px', padding: '2rem', width: '100%' }}>
                <header style={{ marginBottom: '2.5rem' }}>
                    <h1 style={{ fontSize: '1.875rem', fontWeight: '700', marginBottom: '0.5rem' }}>Admin Management</h1>
                    <p style={{ color: 'var(--text-muted)' }}>Manage users who have administrative access to this dashboard.</p>
                </header>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1.5fr', gap: '2rem' }}>
                    <div className="glass-card">
                        <h2 style={{ fontSize: '1.25rem', fontWeight: '600', marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <UserPlus size={20} /> Add New Admin
                        </h2>
                        <form onSubmit={handleAddAdmin}>
                            <div style={{ marginBottom: '1.25rem' }}>
                                <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>User UID</label>
                                <input
                                    type="text"
                                    placeholder="Paste Firebase User UID"
                                    style={{ width: '100%' }}
                                    value={newAdminId}
                                    onChange={(e) => setNewAdminId(e.target.value)}
                                    required
                                />
                            </div>
                            <div style={{ marginBottom: '1.5rem' }}>
                                <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem' }}>Email Address</label>
                                <input
                                    type="email"
                                    placeholder="admin@example.com"
                                    style={{ width: '100%' }}
                                    value={newAdminEmail}
                                    onChange={(e) => setNewAdminEmail(e.target.value)}
                                    required
                                />
                            </div>
                            <button type="submit" className="btn-primary" style={{ width: '100%' }}>
                                Grant Admin Access
                            </button>
                        </form>
                    </div>

                    <div className="glass-card" style={{ padding: '0', overflow: 'hidden' }}>
                        <div style={{ padding: '1.5rem', borderBottom: '1px solid var(--border)' }}>
                            <h2 style={{ fontSize: '1.25rem', fontWeight: '600', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                <Shield size={20} /> Current Admins
                            </h2>
                        </div>
                        <div style={{ maxHeight: '500px', overflowY: 'auto' }}>
                            {loading ? (
                                <p style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>Loading admins...</p>
                            ) : admins.length === 0 ? (
                                <p style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>No other admins found.</p>
                            ) : (
                                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                                    <tbody>
                                        {admins.map((admin) => (
                                            <tr key={admin.id} style={{ borderBottom: '1px solid var(--border)' }}>
                                                <td style={{ padding: '1rem 1.5rem' }}>
                                                    <div style={{ fontWeight: '600' }}>{admin.email}</div>
                                                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>UID: {admin.id}</div>
                                                </td>
                                                <td style={{ padding: '1rem 1.5rem', textAlign: 'right' }}>
                                                    <button
                                                        onClick={() => handleDeleteAdmin(admin.id)}
                                                        style={{ background: 'transparent', color: 'var(--danger)' }}
                                                    >
                                                        <Trash2 size={18} />
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            )}
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default AdminManagement;

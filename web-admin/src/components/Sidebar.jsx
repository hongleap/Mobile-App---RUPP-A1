import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { LayoutDashboard, Package, PlusCircle, LogOut, ShoppingBag, Shield } from 'lucide-react';
import { auth } from '../config/firebase';
import { signOut } from 'firebase/auth';

const Sidebar = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const handleLogout = async () => {
        await signOut(auth);
        navigate('/login');
    };

    const navItems = [
        { path: '/', icon: <LayoutDashboard size={20} />, label: 'Overview' },
        { path: '/products', icon: <Package size={20} />, label: 'Products' },
        { path: '/add-product', icon: <PlusCircle size={20} />, label: 'Add Product' },
        { path: '/admins', icon: <Shield size={20} />, label: 'Admins' },
    ];

    return (
        <div style={{
            width: '260px',
            height: '100vh',
            background: 'var(--bg-card)',
            borderRight: '1px solid var(--border)',
            padding: '2rem 1rem',
            display: 'flex',
            flexDirection: 'column',
            position: 'fixed',
            left: 0,
            top: 0
        }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '3rem', padding: '0 1rem' }}>
                <div style={{ background: 'var(--primary)', padding: '0.6rem', borderRadius: '10px' }}>
                    <ShoppingBag size={28} color="white" />
                </div>
                <h2 style={{ fontSize: '1.5rem', fontWeight: '800', letterSpacing: '-0.02em' }}>StoreAdmin</h2>
            </div>

            <nav style={{ flex: 1 }}>
                {navItems.map((item) => (
                    <Link
                        key={item.path}
                        to={item.path}
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '1rem',
                            padding: '1rem 1.25rem',
                            borderRadius: '10px',
                            color: location.pathname === item.path ? 'white' : 'var(--text-muted)',
                            background: location.pathname === item.path ? 'var(--primary)' : 'transparent',
                            textDecoration: 'none',
                            marginBottom: '0.75rem',
                            transition: 'all 0.2s ease',
                            fontSize: '1.1rem'
                        }}
                    >
                        {React.cloneElement(item.icon, { size: 24 })}
                        <span style={{ fontWeight: '600' }}>{item.label}</span>
                    </Link>
                ))}
            </nav>

            <button
                onClick={handleLogout}
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '1rem',
                    padding: '1rem 1.25rem',
                    borderRadius: '10px',
                    color: 'var(--danger)',
                    background: 'transparent',
                    width: '100%',
                    textAlign: 'left',
                    marginTop: 'auto',
                    fontSize: '1.1rem'
                }}
            >
                <LogOut size={24} />
                <span style={{ fontWeight: '600' }}>Logout</span>
            </button>
        </div>
    );
};

export default Sidebar;

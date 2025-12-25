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
        { path: '/', icon: <LayoutDashboard size={22} />, label: 'Overview' },
        { path: '/products', icon: <Package size={22} />, label: 'Products' },
        { path: '/add-product', icon: <PlusCircle size={22} />, label: 'Add Product' },
        { path: '/banners', icon: <LayoutDashboard size={22} />, label: 'Banners' },
        { path: '/admins', icon: <Shield size={22} />, label: 'Admins' },
    ];

    return (
        <div style={{
            width: '260px',
            height: '100vh',
            background: 'var(--bg-card)',
            borderRight: '1px solid var(--border)',
            padding: '2.5rem 1.25rem',
            display: 'flex',
            flexDirection: 'column',
            position: 'fixed',
            left: 0,
            top: 0,
            zIndex: 100
        }}>
            <div style={{
                display: 'flex',
                alignItems: 'center',
                gap: '1rem',
                marginBottom: '3.5rem',
                padding: '0 0.75rem'
            }} className="animate-slide-up">
                <div style={{
                    background: 'linear-gradient(135deg, var(--primary), var(--primary-hover))',
                    padding: '0.7rem',
                    borderRadius: '14px',
                    boxShadow: '0 8px 16px -4px rgba(99, 102, 241, 0.5)'
                }}>
                    <ShoppingBag size={26} color="white" />
                </div>
                <h2 style={{
                    fontSize: '1.6rem',
                    fontWeight: '900',
                    letterSpacing: '-0.03em',
                    color: 'var(--text-main)'
                }}>
                    StoreAdmin
                </h2>
            </div>

            <nav className="animate-slide-up" style={{ flex: 1, animationDelay: '100ms' }}>
                {navItems.map((item) => {
                    const isActive = location.pathname === item.path;
                    return (
                        <Link
                            key={item.path}
                            to={item.path}
                            style={{
                                display: 'flex',
                                alignItems: 'center',
                                gap: '1rem',
                                padding: '0.85rem 1.25rem',
                                borderRadius: '14px',
                                color: isActive ? 'white' : 'var(--text-muted)',
                                background: isActive ? 'var(--primary)' : 'transparent',
                                textDecoration: 'none',
                                marginBottom: '0.6rem',
                                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                                fontSize: '1.05rem',
                                boxShadow: isActive ? '0 10px 15px -3px rgba(99, 102, 241, 0.3)' : 'none',
                                border: '1px solid',
                                borderColor: isActive ? 'var(--primary)' : 'transparent'
                            }}
                            onMouseEnter={(e) => {
                                if (!isActive) {
                                    e.currentTarget.style.background = 'rgba(255, 255, 255, 0.05)';
                                    e.currentTarget.style.color = 'var(--text-main)';
                                }
                            }}
                            onMouseLeave={(e) => {
                                if (!isActive) {
                                    e.currentTarget.style.background = 'transparent';
                                    e.currentTarget.style.color = 'var(--text-muted)';
                                }
                            }}
                        >
                            {item.icon}
                            <span style={{ fontWeight: isActive ? '700' : '600' }}>{item.label}</span>
                        </Link>
                    );
                })}
            </nav>

            <button
                onClick={handleLogout}
                className="animate-slide-up"
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '1rem',
                    padding: '1rem 1.25rem',
                    borderRadius: '14px',
                    color: 'var(--danger)',
                    background: 'rgba(239, 68, 68, 0.05)',
                    width: '100%',
                    textAlign: 'left',
                    marginTop: 'auto',
                    fontSize: '1.05rem',
                    border: '1px solid transparent',
                    animationDelay: '200ms'
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
                <LogOut size={22} />
                <span style={{ fontWeight: '700' }}>Logout</span>
            </button>
        </div>
    );
};

export default Sidebar;

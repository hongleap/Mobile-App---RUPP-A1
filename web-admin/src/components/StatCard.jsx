import React from 'react';

const StatCard = ({ icon, label, value, color, loading, delay = 0 }) => (
    <div
        className="glass-card animate-slide-up"
        style={{
            padding: '1.5rem',
            flex: 1,
            minWidth: '240px',
            animationDelay: `${delay}ms`
        }}
    >
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.25rem' }}>
            <div style={{
                background: `${color}20`,
                padding: '0.75rem',
                borderRadius: '14px',
                display: 'flex',
                color: color,
                boxShadow: `0 8px 16px -4px ${color}40`
            }}>
                {icon}
            </div>
            <span style={{ color: 'var(--text-muted)', fontSize: '0.95rem', fontWeight: '600', letterSpacing: '0.01em' }}>
                {label}
            </span>
        </div>
        <h3 style={{
            fontSize: '2.25rem',
            fontWeight: '800',
            letterSpacing: '-0.02em',
            color: 'var(--text-main)'
        }}>
            {loading ? (
                <span style={{ opacity: 0.3 }}>...</span>
            ) : (
                value
            )}
        </h3>
    </div>
);

export default StatCard;

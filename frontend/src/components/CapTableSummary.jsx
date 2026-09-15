import React from 'react';
import { DollarSign, Layers, Users, Hash } from 'lucide-react';

export default function CapTableSummary({ capTable }) {
  if (!capTable) return null;

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0
    }).format(val || 0);
  };

  const formatShares = (val) => {
    return new Intl.NumberFormat('en-US').format(val || 0);
  };

  const baselinePPS = (capTable.valuation && capTable.totalShares > 0)
    ? (capTable.valuation / capTable.totalShares).toFixed(2)
    : '0.00';

  return (
    <div className="summary-grid">
      <div className="card stat-card">
        <div className="stat-content">
          <div className="stat-header">
            <span className="stat-label">Enterprise Valuation</span>
            <DollarSign size={15} className="text-muted" />
          </div>
          <span className="stat-value mono">{formatCurrency(capTable.valuation)}</span>
          <span className="stat-meta">Active pre-round valuation</span>
        </div>
      </div>

      <div className="card stat-card">
        <div className="stat-content">
          <div className="stat-header">
            <span className="stat-label">Issued &amp; Outstanding</span>
            <Layers size={15} className="text-muted" />
          </div>
          <span className="stat-value mono">{formatShares(capTable.totalShares)}</span>
          <span className="stat-meta">Total authorized shares</span>
        </div>
      </div>

      <div className="card stat-card">
        <div className="stat-content">
          <div className="stat-header">
            <span className="stat-label">Price Per Share</span>
            <Hash size={15} className="text-muted" />
          </div>
          <span className="stat-value mono">₹{baselinePPS}</span>
          <span className="stat-meta">Current share baseline</span>
        </div>
      </div>

      <div className="card stat-card">
        <div className="stat-content">
          <div className="stat-header">
            <span className="stat-label">Cap Table Stakeholders</span>
            <Users size={15} className="text-muted" />
          </div>
          <span className="stat-value mono">{capTable.stakeholders ? capTable.stakeholders.length : 0}</span>
          <span className="stat-meta">Registered shareholders</span>
        </div>
      </div>
    </div>
  );
}

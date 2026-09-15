import React from 'react';
import { DollarSign, PieChart, Users, TrendingUp } from 'lucide-react';

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
        <div className="stat-icon-wrap icon-blue">
          <DollarSign size={20} />
        </div>
        <div className="stat-content">
          <span className="stat-label">Current Valuation</span>
          <span className="stat-value mono">{formatCurrency(capTable.valuation)}</span>
          <span className="stat-meta">Pre-money cap basis</span>
        </div>
      </div>

      <div className="card stat-card">
        <div className="stat-icon-wrap icon-green">
          <PieChart size={20} />
        </div>
        <div className="stat-content">
          <span className="stat-label">Total Issued Shares</span>
          <span className="stat-value mono">{formatShares(capTable.totalShares)}</span>
          <span className="stat-meta">Common &amp; Preferred equity</span>
        </div>
      </div>

      <div className="card stat-card">
        <div className="stat-icon-wrap icon-purple">
          <Users size={20} />
        </div>
        <div className="stat-content">
          <span className="stat-label">Cap Table Stakeholders</span>
          <span className="stat-value mono">{capTable.stakeholders ? capTable.stakeholders.length : 0}</span>
          <span className="stat-meta">Founders, Employees, Investors</span>
        </div>
      </div>

      <div className="card stat-card">
        <div className="stat-icon-wrap icon-amber">
          <TrendingUp size={20} />
        </div>
        <div className="stat-content">
          <span className="stat-label">Baseline PPS</span>
          <span className="stat-value mono">₹{baselinePPS}</span>
          <span className="stat-meta">Per share valuation</span>
        </div>
      </div>
    </div>
  );
}

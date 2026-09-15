import React from 'react';
import { CheckCircle2, TrendingUp, DollarSign, Award, Layers } from 'lucide-react';

export default function SimulationResults({ result }) {
  if (!result) return null;

  const formatCurrency = (val) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 2
    }).format(val || 0);
  };

  const formatNumber = (val) => {
    return Number(val || 0).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 4 });
  };

  return (
    <div className="card results-card">
      <div className="card-header">
        <div className="results-header-badge">
          <CheckCircle2 size={18} className="text-success" />
          <span>Simulation Outcome</span>
        </div>
        <h2 className="card-title">Round Economics &amp; Pricing Breakdown</h2>
      </div>

      <div className="summary-banner">
        <p className="summary-text">{result.summary}</p>
      </div>

      <div className="results-grid">
        <div className="result-tile">
          <div className="result-tile-header">
            <DollarSign size={16} className="text-primary" />
            <span>Post-Money Valuation</span>
          </div>
          <div className="result-tile-value mono">{formatCurrency(result.postMoneyValuation)}</div>
          <div className="result-tile-sub">Pre-Money ({formatCurrency(result.preMoneyValuation)}) + Investment ({formatCurrency(result.investmentAmount)})</div>
        </div>

        <div className="result-tile">
          <div className="result-tile-header">
            <TrendingUp size={16} className="text-green" />
            <span>Price Per Share (PPS)</span>
          </div>
          <div className="result-tile-value mono">₹{formatNumber(result.pricePerShare)}</div>
          <div className="result-tile-sub">Pre-Money Valuation / Pre-Money Shares ({formatNumber(result.totalPreMoneyShares)})</div>
        </div>

        <div className="result-tile">
          <div className="result-tile-header">
            <Layers size={16} className="text-amber" />
            <span>New Shares Issued</span>
          </div>
          <div className="result-tile-value mono">{formatNumber(result.newSharesIssued)}</div>
          <div className="result-tile-sub">Investment Amount / Price Per Share</div>
        </div>

        <div className="result-tile highlight-tile">
          <div className="result-tile-header">
            <Award size={16} className="text-purple" />
            <span>{result.newInvestorName} Stake</span>
          </div>
          <div className="result-tile-value mono text-purple">{Number(result.newInvestorOwnershipPercentage).toFixed(4)}%</div>
          <div className="result-tile-sub">{result.newInvestorShareClass} shares across {formatNumber(result.totalPostMoneyShares)} total shares</div>
        </div>
      </div>
    </div>
  );
}

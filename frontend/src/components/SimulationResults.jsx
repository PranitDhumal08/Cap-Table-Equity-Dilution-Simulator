import React, { useState } from 'react';
import { CheckCircle2, TrendingUp, DollarSign, Award, Layers, ShieldCheck, ArrowRight, Loader2 } from 'lucide-react';

export default function SimulationResults({ result, onExecute, executing }) {
  const [showConfirm, setShowConfirm] = useState(false);

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

  const handleConfirmExecute = () => {
    setShowConfirm(false);
    if (onExecute) {
      onExecute();
    }
  };

  return (
    <div className="card results-card">
      <div className="card-header flex-between">
        <div>
          <div className="flex-row gap-2">
            <h2 className="card-title">Simulated Round Economics</h2>
            <span className="badge-preview">Preview Mode</span>
          </div>
          <p className="card-description">Verified post-money pricing, dilution breakdown, and issuance schedule</p>
        </div>

        <div className="action-buttons-wrap">
          {!showConfirm ? (
            <button
              onClick={() => setShowConfirm(true)}
              disabled={executing}
              className="btn-commit"
              title="Atomically commit this round into PostgreSQL"
            >
              <ShieldCheck size={16} />
              <span>Commit &amp; Execute Round</span>
            </button>
          ) : (
            <div className="confirm-box">
              <span className="confirm-text">Commit this round to PostgreSQL?</span>
              <button
                onClick={handleConfirmExecute}
                disabled={executing}
                className="btn-confirm-yes"
              >
                {executing ? <Loader2 size={14} className="spin" /> : 'Confirm Commit'}
              </button>
              <button
                onClick={() => setShowConfirm(false)}
                disabled={executing}
                className="btn-confirm-cancel"
              >
                Cancel
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="summary-banner">
        <p className="summary-text">{result.summary}</p>
      </div>

      <div className="results-grid">
        <div className="result-tile">
          <div className="result-tile-header">
            <DollarSign size={15} className="text-muted" />
            <span>Post-Money Valuation</span>
          </div>
          <div className="result-tile-value mono">{formatCurrency(result.postMoneyValuation)}</div>
          <div className="result-tile-sub">Pre-Money ({formatCurrency(result.preMoneyValuation)}) + Investment ({formatCurrency(result.investmentAmount)})</div>
        </div>

        <div className="result-tile">
          <div className="result-tile-header">
            <TrendingUp size={15} className="text-muted" />
            <span>Price Per Share (PPS)</span>
          </div>
          <div className="result-tile-value mono">₹{formatNumber(result.pricePerShare)}</div>
          <div className="result-tile-sub">Pre-Money Valuation / Pre-Money Shares ({formatNumber(result.totalPreMoneyShares)})</div>
        </div>

        <div className="result-tile">
          <div className="result-tile-header">
            <Layers size={15} className="text-muted" />
            <span>New Shares Issued</span>
          </div>
          <div className="result-tile-value mono">{formatNumber(result.newSharesIssued)}</div>
          <div className="result-tile-sub">Investment Amount / Price Per Share</div>
        </div>

        <div className="result-tile highlight-tile">
          <div className="result-tile-header">
            <Award size={15} className="text-primary" />
            <span>{result.newInvestorName} Stake</span>
          </div>
          <div className="result-tile-value mono text-primary">{Number(result.newInvestorOwnershipPercentage).toFixed(4)}%</div>
          <div className="result-tile-sub">{result.newInvestorShareClass} shares across {formatNumber(result.totalPostMoneyShares)} total shares</div>
        </div>
      </div>
    </div>
  );
}

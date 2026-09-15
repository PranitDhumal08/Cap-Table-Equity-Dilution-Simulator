import React, { useState, useEffect } from 'react';
import { Play, Sparkles, AlertCircle } from 'lucide-react';

export default function RoundSimulatorForm({ currentValuation, onSimulate, loading }) {
  const [preMoneyValuation, setPreMoneyValuation] = useState('');
  const [investmentAmount, setInvestmentAmount] = useState('');
  const [investorName, setInvestorName] = useState('Alpha Ventures');
  const [investorType, setInvestorType] = useState('VC');
  const [shareClass, setShareClass] = useState('PREFERRED');
  const [formError, setFormError] = useState(null);

  useEffect(() => {
    if (currentValuation && !preMoneyValuation) {
      setPreMoneyValuation(currentValuation.toString());
      setInvestmentAmount('10000000');
    }
  }, [currentValuation]);

  const handleSubmit = (e) => {
    e.preventDefault();
    setFormError(null);

    const preMoney = parseFloat(preMoneyValuation);
    const investment = parseFloat(investmentAmount);

    if (isNaN(preMoney) || preMoney <= 0) {
      setFormError('Pre-money valuation must be a positive number.');
      return;
    }

    if (isNaN(investment) || investment <= 0) {
      setFormError('Investment amount must be a positive number.');
      return;
    }

    if (!investorName.trim()) {
      setFormError('Investor name cannot be blank.');
      return;
    }

    onSimulate({
      preMoneyValuation: preMoney,
      investmentAmount: investment,
      investorName: investorName.trim(),
      investorType,
      shareClass,
    });
  };

  const handleLoadDemo = () => {
    setPreMoneyValuation('40000000');
    setInvestmentAmount('10000000');
    setInvestorName('Alpha Ventures');
    setInvestorType('VC');
    setShareClass('PREFERRED');
    setFormError(null);
  };

  return (
    <div className="card form-card">
      <div className="card-header flex-between">
        <div>
          <h2 className="card-title">Venture Round Simulator</h2>
          <p className="card-description">Model a hypothetical equity financing round (idempotent read-only engine)</p>
        </div>
        <button 
          type="button" 
          onClick={handleLoadDemo} 
          className="btn-secondary btn-sm"
          title="Load standard Series A example"
        >
          <Sparkles size={14} />
          <span>Load Series A Preset</span>
        </button>
      </div>

      {formError && (
        <div className="alert-box alert-danger">
          <AlertCircle size={16} />
          <span>{formError}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="simulator-form">
        <div className="form-grid">
          <div className="form-group">
            <label className="form-label">Pre-Money Valuation (INR ₹)</label>
            <input
              type="number"
              className="form-input mono"
              value={preMoneyValuation}
              onChange={(e) => setPreMoneyValuation(e.target.value)}
              placeholder="e.g. 40000000"
              step="any"
              required
            />
            <span className="input-hint">Agreed company valuation prior to investment</span>
          </div>

          <div className="form-group">
            <label className="form-label">Investment Capital (INR ₹)</label>
            <input
              type="number"
              className="form-input mono"
              value={investmentAmount}
              onChange={(e) => setInvestmentAmount(e.target.value)}
              placeholder="e.g. 10000000"
              step="any"
              required
            />
            <span className="input-hint">New cash injected by incoming investor</span>
          </div>

          <div className="form-group">
            <label className="form-label">Lead Investor Name</label>
            <input
              type="text"
              className="form-input"
              value={investorName}
              onChange={(e) => setInvestorName(e.target.value)}
              placeholder="e.g. Alpha Ventures"
              required
            />
            <span className="input-hint">Entity or syndicate injecting capital</span>
          </div>

          <div className="form-group">
            <label className="form-label">Investor Category</label>
            <select
              className="form-input"
              value={investorType}
              onChange={(e) => setInvestorType(e.target.value)}
            >
              <option value="VC">VC (Venture Capital)</option>
              <option value="ANGEL">Angel Investor</option>
              <option value="FOUNDER">Founder Follow-on</option>
              <option value="EMPLOYEE">Employee / Management</option>
            </select>
            <span className="input-hint">Classification for equity categorization</span>
          </div>

          <div className="form-group">
            <label className="form-label">Share Class Issued</label>
            <select
              className="form-input"
              value={shareClass}
              onChange={(e) => setShareClass(e.target.value)}
            >
              <option value="PREFERRED">Series Preferred (Standard for Institutional VCs)</option>
              <option value="COMMON">Common Shares</option>
            </select>
            <span className="input-hint">Seniority class for new shares</span>
          </div>
        </div>

        <div className="form-actions">
          <button type="submit" disabled={loading} className="btn-primary">
            <Play size={16} />
            <span>{loading ? 'Executing Financial Calculation...' : 'Simulate Funding Round'}</span>
          </button>
        </div>
      </form>
    </div>
  );
}

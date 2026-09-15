import React, { useState, useEffect } from 'react';
import { Play, RotateCcw, AlertCircle } from 'lucide-react';

export default function RoundSimulatorForm({ currentValuation, onSimulate, loading }) {
  const [roundName, setRoundName] = useState('Series A Preferred');
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
      roundName: roundName.trim(),
      preMoneyValuation: preMoney,
      investmentAmount: investment,
      investorName: investorName.trim(),
      investorType,
      shareClass,
    });
  };

  const handleReset = () => {
    setRoundName('Series A Preferred');
    setPreMoneyValuation(currentValuation ? currentValuation.toString() : '40000000');
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
          <h2 className="card-title">Financing Round Parameters</h2>
          <p className="card-description">Configure pre-money valuation, investment capital, and incoming investor terms</p>
        </div>
        <button 
          type="button" 
          onClick={handleReset} 
          className="btn-secondary btn-sm"
          title="Reset to default NovaFin terms"
        >
          <RotateCcw size={13} />
          <span>Reset Preset</span>
        </button>
      </div>

      {formError && (
        <div className="alert-box alert-danger">
          <AlertCircle size={15} />
          <span>{formError}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="simulator-form">
        <div className="form-grid">
          <div className="form-group">
            <label className="form-label">Round Title</label>
            <input
              type="text"
              className="form-input"
              value={roundName}
              onChange={(e) => setRoundName(e.target.value)}
              placeholder="e.g. Series A Preferred"
              required
            />
            <span className="input-hint">Venture round classification</span>
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
            <span className="input-hint">Institutional fund or syndicate</span>
          </div>

          <div className="form-group">
            <label className="form-label">Pre-Money Valuation (INR ₹)</label>
            <input
              type="number"
              className="form-input mono"
              value={preMoneyValuation}
              onChange={(e) => setPreMoneyValuation(e.target.value)}
              placeholder="40000000"
              step="any"
              required
            />
            <span className="input-hint">Agreed enterprise valuation prior to round</span>
          </div>

          <div className="form-group">
            <label className="form-label">New Capital Investment (INR ₹)</label>
            <input
              type="number"
              className="form-input mono"
              value={investmentAmount}
              onChange={(e) => setInvestmentAmount(e.target.value)}
              placeholder="10000000"
              step="any"
              required
            />
            <span className="input-hint">Cash invested in exchange for newly issued shares</span>
          </div>

          <div className="form-group">
            <label className="form-label">Stakeholder Category</label>
            <select
              className="form-input"
              value={investorType}
              onChange={(e) => setInvestorType(e.target.value)}
            >
              <option value="VC">VC (Venture Capital Fund)</option>
              <option value="ANGEL">Angel Investor</option>
              <option value="FOUNDER">Founder Follow-on</option>
              <option value="EMPLOYEE">Management / Employee</option>
            </select>
            <span className="input-hint">Cap table category</span>
          </div>

          <div className="form-group">
            <label className="form-label">Share Class Issued</label>
            <select
              className="form-input"
              value={shareClass}
              onChange={(e) => setShareClass(e.target.value)}
            >
              <option value="PREFERRED">Preferred Stock (Institutional Standard)</option>
              <option value="COMMON">Common Stock</option>
            </select>
            <span className="input-hint">Seniority &amp; liquidation class</span>
          </div>
        </div>

        <div className="form-actions">
          <button type="submit" disabled={loading} className="btn-primary">
            <Play size={15} />
            <span>{loading ? 'Calculating Round Economics...' : 'Simulate Round Dilution'}</span>
          </button>
        </div>
      </form>
    </div>
  );
}

import React from 'react';
import { Building, RefreshCw } from 'lucide-react';

export default function CompanyHeader({ company, onRefresh, loading }) {
  return (
    <header className="header-container">
      <div className="header-brand">
        <div className="brand-logo">
          <span>CX</span>
        </div>
        <div>
          <div className="brand-title-wrap">
            <h1 className="brand-title">CapTableX</h1>
            <span className="brand-badge">Equity Ledger</span>
          </div>
          <p className="brand-subtitle">Startup Capitalization &amp; Dilution Modeling System</p>
        </div>
      </div>

      <div className="header-actions">
        <div className="company-badge">
          <Building size={14} className="text-muted" />
          <span className="company-name">{company ? company.companyName : 'Loading company...'}</span>
        </div>
        <button 
          onClick={onRefresh} 
          disabled={loading}
          className="btn-icon" 
          title="Refresh Ledger"
        >
          <RefreshCw size={15} className={loading ? 'spin' : ''} />
        </button>
      </div>
    </header>
  );
}

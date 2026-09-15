import React from 'react';
import { Building2, RefreshCw, Layers } from 'lucide-react';

export default function CompanyHeader({ company, onRefresh, loading }) {
  return (
    <header className="header-container">
      <div className="header-brand">
        <div className="brand-icon">
          <Layers size={26} className="text-primary" />
        </div>
        <div>
          <div className="brand-title-wrap">
            <h1 className="brand-title">CapTableX</h1>
            <span className="badge-beta">v1.0 Production Engine</span>
          </div>
          <p className="brand-subtitle">Startup Cap Table &amp; Venture Funding Round Dilution Simulator</p>
        </div>
      </div>

      <div className="header-actions">
        <div className="company-badge">
          <Building2 size={16} />
          <span>{company ? company.companyName : 'Loading...'}</span>
        </div>
        <button 
          onClick={onRefresh} 
          disabled={loading}
          className="btn-icon" 
          title="Refresh Cap Table"
        >
          <RefreshCw size={16} className={loading ? 'spin' : ''} />
        </button>
      </div>
    </header>
  );
}

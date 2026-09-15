import React from 'react';

export default function CapTableTable({ stakeholders, totalShares }) {
  if (!stakeholders || stakeholders.length === 0) {
    return (
      <div className="card empty-card">
        <p className="text-muted">No stakeholders registered on this capitalization table.</p>
      </div>
    );
  }

  const getRoleBadgeClass = (role) => {
    switch (role) {
      case 'FOUNDER': return 'badge-role role-founder';
      case 'VC': return 'badge-role role-vc';
      case 'ANGEL': return 'badge-role role-angel';
      case 'EMPLOYEE': return 'badge-role role-employee';
      default: return 'badge-role';
    }
  };

  return (
    <div className="card table-card">
      <div className="card-header flex-between">
        <div>
          <h2 className="card-title">Shareholder Ownership Ledger</h2>
          <p className="card-description">Persistent equity breakdown verified from PostgreSQL database</p>
        </div>
        <span className="badge-count">{stakeholders.length} Stakeholders</span>
      </div>

      <div className="table-responsive">
        <table className="financial-table">
          <thead>
            <tr>
              <th>Stakeholder</th>
              <th>Category</th>
              <th>Class</th>
              <th className="text-right">Shares Held</th>
              <th className="text-right">Ownership Stake</th>
              <th style={{ width: '160px' }}>Allocation</th>
            </tr>
          </thead>
          <tbody>
            {stakeholders.map((s, idx) => {
              const pct = Number(s.ownershipPercentage || 0);
              return (
                <tr key={s.stakeholderId || idx}>
                  <td className="font-medium text-white">{s.name}</td>
                  <td>
                    <span className={getRoleBadgeClass(s.role)}>
                      {s.role}
                    </span>
                  </td>
                  <td>
                    <span className={`badge-class ${s.shareClass === 'PREFERRED' ? 'class-preferred' : 'class-common'}`}>
                      {s.shareClass}
                    </span>
                  </td>
                  <td className="text-right mono text-white">
                    {Number(s.shares).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 4 })}
                  </td>
                  <td className="text-right mono font-semibold text-primary">
                    {pct.toFixed(4)}%
                  </td>
                  <td>
                    <div className="progress-track">
                      <div 
                        className="progress-fill" 
                        style={{ 
                          width: `${Math.min(pct, 100)}%`,
                          backgroundColor: idx === 0 ? '#3b82f6' : idx === 1 ? '#10b981' : idx === 2 ? '#f59e0b' : '#8b5cf6'
                        }}
                      />
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
          <tfoot>
            <tr className="tfoot-total">
              <td colSpan={3} className="font-semibold text-white">Total Authorized &amp; Issued</td>
              <td className="text-right mono font-semibold text-white">
                {Number(totalShares).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 4 })}
              </td>
              <td className="text-right mono font-semibold text-white">100.0000%</td>
              <td></td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>
  );
}

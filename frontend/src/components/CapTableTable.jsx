import React from 'react';
import { Shield, Award, User, Briefcase } from 'lucide-react';

export default function CapTableTable({ stakeholders, totalShares }) {
  if (!stakeholders || stakeholders.length === 0) {
    return (
      <div className="card empty-card">
        <p>No stakeholders registered on this cap table yet.</p>
      </div>
    );
  }

  const getRoleIcon = (role) => {
    switch (role) {
      case 'FOUNDER': return <Award size={14} />;
      case 'VC': return <Briefcase size={14} />;
      case 'ANGEL': return <Shield size={14} />;
      case 'EMPLOYEE': return <User size={14} />;
      default: return null;
    }
  };

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
      <div className="card-header">
        <div>
          <h2 className="card-title">Current Capitalization Ledger</h2>
          <p className="card-description">Verified baseline shareholdings prior to funding round simulation</p>
        </div>
      </div>

      <div className="table-responsive">
        <table className="financial-table">
          <thead>
            <tr>
              <th>Stakeholder</th>
              <th>Role</th>
              <th>Share Class</th>
              <th className="text-right">Shares Owned</th>
              <th className="text-right">Ownership %</th>
              <th style={{ width: '180px' }}>Distribution</th>
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
                      {getRoleIcon(s.role)}
                      <span>{s.role}</span>
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
                          backgroundColor: idx === 0 ? '#3b82f6' : idx === 1 ? '#10b981' : idx === 2 ? '#f59e0b' : '#a855f7'
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
              <td colSpan={3} className="font-bold">Total Cap Table</td>
              <td className="text-right mono font-bold text-white">
                {Number(totalShares).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 4 })}
              </td>
              <td className="text-right mono font-bold text-white">100.0000%</td>
              <td></td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>
  );
}

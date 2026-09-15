import React from 'react';
import { ArrowDownRight, BarChart2, ShieldAlert } from 'lucide-react';

export default function OwnershipComparisonChart({ result }) {
  if (!result) return null;

  const stakeholders = result.stakeholders || [];
  const investorPct = Number(result.newInvestorOwnershipPercentage || 0);

  // Palette for chart items
  const colors = [
    '#3b82f6', // blue
    '#10b981', // green
    '#f59e0b', // amber
    '#06b6d4', // cyan
    '#ec4899', // pink
    '#8b5cf6', // violet
  ];
  const investorColor = '#a855f7'; // purple

  return (
    <div className="card comparison-card">
      <div className="card-header">
        <div className="flex-between">
          <div>
            <h2 className="card-title">Cap Table Evolution: Before vs. After Round</h2>
            <p className="card-description">
              Visualizing shareholder dilution and equity redistribution from new capital issuance
            </p>
          </div>
          <div className="legend-pills">
            <span className="legend-pill" style={{ borderColor: '#3b82f6', color: '#60a5fa' }}>Founders</span>
            <span className="legend-pill" style={{ borderColor: '#10b981', color: '#34d399' }}>Employees</span>
            <span className="legend-pill" style={{ borderColor: investorColor, color: '#c084fc' }}>New Investor</span>
          </div>
        </div>
      </div>

      {/* Visual Proportional Bars */}
      <div className="visual-bars-container">
        <div className="bar-group">
          <div className="bar-label-wrap">
            <span className="bar-title">BEFORE FUNDING (100.0000%)</span>
            <span className="bar-shares mono">1,000,000 shares</span>
          </div>
          <div className="stacked-bar">
            {stakeholders.map((s, idx) => {
              const prevPct = Number(s.previousOwnershipPercentage || 0);
              const color = colors[idx % colors.length];
              return (
                <div
                  key={s.stakeholderId || idx}
                  className="bar-segment"
                  style={{ width: `${prevPct}%`, backgroundColor: color }}
                  title={`${s.name}: ${prevPct.toFixed(2)}%`}
                >
                  {prevPct >= 10 && <span className="segment-text">{s.name} ({prevPct.toFixed(1)}%)</span>}
                </div>
              );
            })}
          </div>
        </div>

        <div className="bar-group">
          <div className="bar-label-wrap">
            <span className="bar-title">AFTER FUNDING (100.0000%)</span>
            <span className="bar-shares mono">{Number(result.totalPostMoneyShares).toLocaleString()} shares</span>
          </div>
          <div className="stacked-bar">
            {stakeholders.map((s, idx) => {
              const newPct = Number(s.newOwnershipPercentage || 0);
              const color = colors[idx % colors.length];
              return (
                <div
                  key={s.stakeholderId || idx}
                  className="bar-segment"
                  style={{ width: `${newPct}%`, backgroundColor: color }}
                  title={`${s.name}: ${newPct.toFixed(2)}%`}
                >
                  {newPct >= 10 && <span className="segment-text">{s.name} ({newPct.toFixed(1)}%)</span>}
                </div>
              );
            })}
            <div
              className="bar-segment"
              style={{ width: `${investorPct}%`, backgroundColor: investorColor }}
              title={`${result.newInvestorName}: ${investorPct.toFixed(2)}%`}
            >
              {investorPct >= 10 && <span className="segment-text">{result.newInvestorName} ({investorPct.toFixed(1)}%)</span>}
            </div>
          </div>
        </div>
      </div>

      {/* Detailed Comparison Table */}
      <div className="table-responsive" style={{ marginTop: '1.5rem' }}>
        <table className="financial-table comparison-table">
          <thead>
            <tr>
              <th>Stakeholder</th>
              <th>Role</th>
              <th>Share Class</th>
              <th className="text-right">Shares Held</th>
              <th className="text-right">Before %</th>
              <th className="text-right">After %</th>
              <th className="text-right">Dilution (pts)</th>
              <th className="text-right">Relative Dilution %</th>
            </tr>
          </thead>
          <tbody>
            {stakeholders.map((s, idx) => {
              const prev = Number(s.previousOwnershipPercentage || 0);
              const next = Number(s.newOwnershipPercentage || 0);
              const pts = Number(s.dilutionPercentagePoints || 0);
              const rel = Number(s.relativeDilutionPercentage || 0);
              return (
                <tr key={s.stakeholderId || idx}>
                  <td className="font-semibold text-white">{s.name}</td>
                  <td><span className="badge-role">{s.role}</span></td>
                  <td><span className="badge-class class-common">{s.shareClass}</span></td>
                  <td className="text-right mono text-white">
                    {Number(s.shares).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 4 })}
                  </td>
                  <td className="text-right mono text-white">{prev.toFixed(4)}%</td>
                  <td className="text-right mono font-semibold text-primary">{next.toFixed(4)}%</td>
                  <td className="text-right mono text-danger">
                    <span className="dilution-cell">
                      <ArrowDownRight size={14} />
                      -{pts.toFixed(4)}%
                    </span>
                  </td>
                  <td className="text-right mono text-amber font-medium">
                    {rel.toFixed(4)}%
                  </td>
                </tr>
              );
            })}

            {/* Incoming Investor Row */}
            <tr className="tr-investor">
              <td className="font-bold text-purple">{result.newInvestorName} (New)</td>
              <td><span className="badge-role role-vc">{result.newInvestorType}</span></td>
              <td><span className="badge-class class-preferred">{result.newInvestorShareClass}</span></td>
              <td className="text-right mono font-bold text-white">
                {Number(result.newSharesIssued).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 4 })}
              </td>
              <td className="text-right mono text-muted">0.0000%</td>
              <td className="text-right mono font-bold text-purple">{investorPct.toFixed(4)}%</td>
              <td className="text-right mono text-success font-semibold">+{investorPct.toFixed(4)}%</td>
              <td className="text-right mono text-muted">N/A</td>
            </tr>
          </tbody>
          <tfoot>
            <tr className="tfoot-total">
              <td colSpan={3} className="font-bold">Total Post-Money Capitalization</td>
              <td className="text-right mono font-bold text-white">
                {Number(result.totalPostMoneyShares).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 4 })}
              </td>
              <td className="text-right mono font-bold text-white">100.0000%</td>
              <td className="text-right mono font-bold text-white">100.0000%</td>
              <td colSpan={2} className="text-center text-muted font-normal text-xs">
                Zero share mutation for existing stakeholders; ownership denominator expanded
              </td>
            </tr>
          </tfoot>
        </table>
      </div>

      <div className="dilution-explainer">
        <div className="explainer-item">
          <strong>Percentage-Point Dilution:</strong> Absolute subtraction: Previous Ownership % - New Ownership %.
        </div>
        <div className="explainer-item">
          <strong>Relative Dilution %:</strong> Proportional equity reduction: ((Previous % - New %) / Previous %) × 100.
        </div>
      </div>
    </div>
  );
}

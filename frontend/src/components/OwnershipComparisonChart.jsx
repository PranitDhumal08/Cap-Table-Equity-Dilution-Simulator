import React from 'react';
import { ArrowDownRight } from 'lucide-react';

export default function OwnershipComparisonChart({ result }) {
  if (!result) return null;

  const stakeholders = result.stakeholders || [];
  const investorPct = Number(result.newInvestorOwnershipPercentage || 0);

  const colors = [
    '#3b82f6', // blue
    '#10b981', // emerald
    '#f59e0b', // amber
    '#06b6d4', // cyan
    '#ec4899', // pink
    '#8b5cf6', // violet
  ];
  const investorColor = '#6366f1'; // indigo

  return (
    <div className="card comparison-card">
      <div className="card-header flex-between">
        <div>
          <h2 className="card-title">Pre-Round vs. Post-Round Ownership Comparison</h2>
          <p className="card-description">
            Proportional equity redistribution and dilution resulting from newly minted shares
          </p>
        </div>
        <div className="legend-pills">
          <span className="legend-pill pill-founders">Founders</span>
          <span className="legend-pill pill-employees">Employees</span>
          <span className="legend-pill pill-investor">New Investor</span>
        </div>
      </div>

      {/* Visual Proportional Bars */}
      <div className="visual-bars-container">
        <div className="bar-group">
          <div className="bar-label-wrap">
            <span className="bar-title">BEFORE FINANCING</span>
            <span className="bar-shares mono">{Number(result.totalPreMoneyShares).toLocaleString()} shares (100.0000%)</span>
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
            <span className="bar-title">AFTER FINANCING</span>
            <span className="bar-shares mono">{Number(result.totalPostMoneyShares).toLocaleString()} shares (100.0000%)</span>
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

      {/* Comparison Table */}
      <div className="table-responsive" style={{ marginTop: '1.25rem' }}>
        <table className="financial-table comparison-table">
          <thead>
            <tr>
              <th>Stakeholder</th>
              <th>Category</th>
              <th>Class</th>
              <th className="text-right">Shares Held</th>
              <th className="text-right">Pre-Round %</th>
              <th className="text-right">Post-Round %</th>
              <th className="text-right">Net Dilution (pts)</th>
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
                  <td className="font-medium text-white">{s.name}</td>
                  <td><span className="badge-role">{s.role}</span></td>
                  <td><span className="badge-class class-common">{s.shareClass}</span></td>
                  <td className="text-right mono text-white">
                    {Number(s.shares).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 4 })}
                  </td>
                  <td className="text-right mono text-white">{prev.toFixed(4)}%</td>
                  <td className="text-right mono font-semibold text-primary">{next.toFixed(4)}%</td>
                  <td className="text-right mono text-rose">
                    <span className="dilution-cell">
                      <ArrowDownRight size={13} />
                      -{pts.toFixed(4)}%
                    </span>
                  </td>
                  <td className="text-right mono text-amber">
                    {rel.toFixed(4)}%
                  </td>
                </tr>
              );
            })}

            {/* New Investor Row */}
            <tr className="tr-investor">
              <td className="font-semibold text-indigo">{result.newInvestorName} (Incoming)</td>
              <td><span className="badge-role role-vc">{result.newInvestorRole || result.newInvestorType}</span></td>
              <td><span className="badge-class class-preferred">{result.newInvestorShareClass}</span></td>
              <td className="text-right mono font-semibold text-white">
                {Number(result.newSharesIssued).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 4 })}
              </td>
              <td className="text-right mono text-muted">0.0000%</td>
              <td className="text-right mono font-semibold text-indigo">{investorPct.toFixed(4)}%</td>
              <td className="text-right mono text-emerald font-semibold">+{investorPct.toFixed(4)}%</td>
              <td className="text-right mono text-muted">–</td>
            </tr>
          </tbody>
          <tfoot>
            <tr className="tfoot-total">
              <td colSpan={3} className="font-semibold text-white">Total Post-Money Capitalization</td>
              <td className="text-right mono font-semibold text-white">
                {Number(result.totalPostMoneyShares).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 4 })}
              </td>
              <td className="text-right mono font-semibold text-white">100.0000%</td>
              <td className="text-right mono font-semibold text-white">100.0000%</td>
              <td colSpan={2} className="text-center text-muted text-xs">
                Pro-rata equity expansion across total denominator
              </td>
            </tr>
          </tfoot>
        </table>
      </div>

      <div className="dilution-explainer">
        <div className="explainer-item">
          <strong>Net Dilution (Percentage Points):</strong> Absolute drop in ownership percentage: <code>Previous % - New %</code>.
        </div>
        <div className="explainer-item">
          <strong>Relative Dilution %:</strong> Proportion of existing equity given up: <code>(Net Dilution / Previous %) × 100</code>.
        </div>
      </div>
    </div>
  );
}

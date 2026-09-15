import React from 'react';
import { History, CheckCircle2, ArrowUpRight, ShieldCheck } from 'lucide-react';

export default function TransactionHistoryTable({ transactions }) {
  const formatCurrency = (val) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0
    }).format(val || 0);
  };

  const formatNumber = (val) => {
    return Number(val || 0).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 4 });
  };

  const formatDate = (isoStr) => {
    if (!isoStr) return '–';
    const date = new Date(isoStr);
    return date.toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  };

  if (!transactions || transactions.length === 0) {
    return (
      <div className="card empty-audit-card">
        <div className="empty-audit-icon">
          <History size={32} />
        </div>
        <h3 className="empty-title">No Committed Rounds Recorded Yet</h3>
        <p className="empty-desc">
          When you execute and commit a simulated venture financing round, the transaction will be permanently recorded in PostgreSQL and appear here as an immutable audit record.
        </p>
      </div>
    );
  }

  return (
    <div className="card table-card">
      <div className="card-header flex-between">
        <div>
          <div className="flex-row gap-2">
            <h2 className="card-title">Transaction Audit Ledger</h2>
            <span className="badge-count">{transactions.length} Committed</span>
          </div>
          <p className="card-description">
            Permanent, append-only record of venture capital financing rounds committed to the database
          </p>
        </div>
        <div className="audit-verified-badge">
          <ShieldCheck size={14} className="text-emerald" />
          <span>PostgreSQL ACID Audited</span>
        </div>
      </div>

      <div className="table-responsive">
        <table className="financial-table">
          <thead>
            <tr>
              <th>Executed At</th>
              <th>Round</th>
              <th>Investor</th>
              <th>Class</th>
              <th className="text-right">Capital Injected</th>
              <th className="text-right">Share Price</th>
              <th className="text-right">Shares Minted</th>
              <th className="text-right">Post-Valuation</th>
              <th className="text-right">Stake %</th>
              <th>Reference ID</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((tx) => (
              <tr key={tx.transactionId}>
                <td className="mono text-muted text-xs whitespace-nowrap">
                  {formatDate(tx.executedAt)}
                </td>
                <td className="font-medium text-white">
                  <span className="round-pill">{tx.roundName || 'Financing Round'}</span>
                </td>
                <td>
                  <div className="investor-cell">
                    <span className="font-medium text-white">{tx.investorName}</span>
                    <span className="badge-role role-vc">{tx.investorRole}</span>
                  </div>
                </td>
                <td>
                  <span className={`badge-class ${tx.shareClass === 'PREFERRED' ? 'class-preferred' : 'class-common'}`}>
                    {tx.shareClass}
                  </span>
                </td>
                <td className="text-right mono font-semibold text-emerald">
                  {formatCurrency(tx.investmentAmount)}
                </td>
                <td className="text-right mono text-white">
                  ₹{formatNumber(tx.pricePerShare)}
                </td>
                <td className="text-right mono text-white">
                  {formatNumber(tx.sharesIssued)}
                </td>
                <td className="text-right mono text-white">
                  {formatCurrency(tx.postMoneyValuation)}
                </td>
                <td className="text-right mono font-semibold text-primary">
                  {Number(tx.investorOwnershipPct).toFixed(4)}%
                </td>
                <td className="mono text-xs text-muted" title={tx.transactionId}>
                  {tx.transactionId ? `${tx.transactionId.substring(0, 8)}...` : '–'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

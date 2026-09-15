import React, { useState, useEffect } from 'react';
import CompanyHeader from './components/CompanyHeader';
import CapTableSummary from './components/CapTableSummary';
import CapTableTable from './components/CapTableTable';
import RoundSimulatorForm from './components/RoundSimulatorForm';
import SimulationResults from './components/SimulationResults';
import OwnershipComparisonChart from './components/OwnershipComparisonChart';
import TransactionHistoryTable from './components/TransactionHistoryTable';
import { fetchCompanies, fetchCapTable, simulateFundingRound, executeFundingRound, fetchTransactionHistory } from './services/api';
import { Table, Sliders, History, CheckCircle2 } from 'lucide-react';
import './App.css';

const DEFAULT_COMPANY_ID = 'a1b2c3d4-0001-4000-8000-000000000001';

export default function App() {
  const [companies, setCompanies] = useState([]);
  const [selectedCompanyId, setSelectedCompanyId] = useState(DEFAULT_COMPANY_ID);
  const [capTable, setCapTable] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [simulationResult, setSimulationResult] = useState(null);
  const [lastFormValues, setLastFormValues] = useState(null);
  const [activeTab, setActiveTab] = useState('overview'); // 'overview' | 'simulator' | 'transactions'

  const [loading, setLoading] = useState(false);
  const [simulating, setSimulating] = useState(false);
  const [executing, setExecuting] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    setLoading(true);
    setError(null);
    try {
      const companyList = await fetchCompanies().catch(() => []);
      setCompanies(companyList);
      
      const targetId = (companyList && companyList.length > 0)
        ? companyList[0].companyId
        : DEFAULT_COMPANY_ID;
      
      setSelectedCompanyId(targetId);
      await Promise.all([
        loadCapTable(targetId),
        loadTransactions(targetId)
      ]);
    } catch (err) {
      setError(err.message || 'Failed to connect to CapTableX API backend.');
    } finally {
      setLoading(false);
    }
  };

  const loadCapTable = async (companyId) => {
    try {
      const data = await fetchCapTable(companyId);
      setCapTable(data);
    } catch (err) {
      setError(err.message || 'Error fetching cap table.');
    }
  };

  const loadTransactions = async (companyId) => {
    try {
      const txs = await fetchTransactionHistory(companyId);
      setTransactions(txs || []);
    } catch (err) {
      console.warn('Could not load transactions:', err);
    }
  };

  const handleSimulate = async (formValues) => {
    setSimulating(true);
    setError(null);
    setSuccessMessage(null);
    setLastFormValues(formValues);
    try {
      const payload = {
        companyId: selectedCompanyId,
        ...formValues,
      };
      const result = await simulateFundingRound(payload);
      setSimulationResult(result);
    } catch (err) {
      setError(err.message || 'Failed to execute round simulation.');
    } finally {
      setSimulating(false);
    }
  };

  const handleExecuteRound = async () => {
    if (!simulationResult || !lastFormValues) return;
    setExecuting(true);
    setError(null);
    try {
      const payload = {
        companyId: selectedCompanyId,
        roundName: lastFormValues.roundName || 'Series A Preferred',
        preMoneyValuation: simulationResult.preMoneyValuation,
        investmentAmount: simulationResult.investmentAmount,
        investorName: simulationResult.newInvestorName,
        investorType: simulationResult.newInvestorRole || simulationResult.newInvestorType,
        shareClass: simulationResult.newInvestorShareClass,
      };

      await executeFundingRound(payload);
      
      setSuccessMessage(`Financing round "${payload.roundName}" committed to PostgreSQL. Cap table and valuation updated.`);
      
      // Refresh cap table and transactions from database
      await loadCapTable(selectedCompanyId);
      await loadTransactions(selectedCompanyId);
      
      // Navigate to transaction audit ledger
      setActiveTab('transactions');
    } catch (err) {
      setError(err.message || 'Failed to commit funding round to database.');
    } finally {
      setExecuting(false);
    }
  };

  const handleRefresh = async () => {
    setLoading(true);
    await Promise.all([
      loadCapTable(selectedCompanyId),
      loadTransactions(selectedCompanyId)
    ]);
    setLoading(false);
  };

  return (
    <div className="app-layout">
      <CompanyHeader
        company={capTable}
        onRefresh={handleRefresh}
        loading={loading}
      />

      <main className="main-content">
        {error && (
          <div className="global-error-banner">
            <span>{error}</span>
            <button onClick={() => setError(null)} className="btn-text">Dismiss</button>
          </div>
        )}

        {successMessage && (
          <div className="global-success-banner">
            <div className="flex-row gap-2">
              <CheckCircle2 size={16} />
              <span>{successMessage}</span>
            </div>
            <button onClick={() => setSuccessMessage(null)} className="btn-text">Dismiss</button>
          </div>
        )}

        {/* Top Summary Metrics */}
        <CapTableSummary capTable={capTable} />

        {/* Institutional Tab Navigation */}
        <nav className="tab-navigation">
          <button
            className={`tab-button ${activeTab === 'overview' ? 'tab-active' : ''}`}
            onClick={() => setActiveTab('overview')}
          >
            <Table size={15} />
            <span>Cap Table Ledger</span>
          </button>
          <button
            className={`tab-button ${activeTab === 'simulator' ? 'tab-active' : ''}`}
            onClick={() => setActiveTab('simulator')}
          >
            <Sliders size={15} />
            <span>Venture Round Simulator</span>
          </button>
          <button
            className={`tab-button ${activeTab === 'transactions' ? 'tab-active' : ''}`}
            onClick={() => setActiveTab('transactions')}
          >
            <History size={15} />
            <span>Transaction Audit Log</span>
            {transactions.length > 0 && (
              <span className="tab-count-badge">{transactions.length}</span>
            )}
          </button>
        </nav>

        {/* Tab 1: Cap Table Overview */}
        {activeTab === 'overview' && (
          <div className="tab-pane">
            <CapTableTable
              stakeholders={capTable ? capTable.stakeholders : []}
              totalShares={capTable ? capTable.totalShares : 0}
            />
          </div>
        )}

        {/* Tab 2: Venture Round Simulator */}
        {activeTab === 'simulator' && (
          <div className="tab-pane">
            <RoundSimulatorForm
              currentValuation={capTable ? capTable.valuation : null}
              onSimulate={handleSimulate}
              loading={simulating}
            />

            {simulationResult && (
              <div className="simulation-results-block" style={{ marginTop: '1.5rem' }}>
                <SimulationResults 
                  result={simulationResult} 
                  onExecute={handleExecuteRound}
                  executing={executing}
                />
                <OwnershipComparisonChart result={simulationResult} />
              </div>
            )}
          </div>
        )}

        {/* Tab 3: Transaction Audit Ledger */}
        {activeTab === 'transactions' && (
          <div className="tab-pane">
            <TransactionHistoryTable transactions={transactions} />
          </div>
        )}
      </main>

      <footer className="footer-container">
        <div className="footer-content">
          <p className="footer-title">CapTableX – Institutional Equity &amp; Dilution Modeling</p>
          <p className="footer-sub text-muted">
            Strict BigDecimal Decimal Arithmetic • PostgreSQL ACID Ledger • Zero Premature Rounding
          </p>
        </div>
      </footer>
    </div>
  );
}

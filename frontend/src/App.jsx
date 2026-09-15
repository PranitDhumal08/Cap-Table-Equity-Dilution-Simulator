import React, { useState, useEffect } from 'react';
import CompanyHeader from './components/CompanyHeader';
import CapTableSummary from './components/CapTableSummary';
import CapTableTable from './components/CapTableTable';
import RoundSimulatorForm from './components/RoundSimulatorForm';
import SimulationResults from './components/SimulationResults';
import OwnershipComparisonChart from './components/OwnershipComparisonChart';
import { fetchCompanies, fetchCapTable, simulateFundingRound } from './services/api';
import './App.css';

// Default seeded company ID for NovaFin Technologies
const DEFAULT_COMPANY_ID = 'a1b2c3d4-0001-4000-8000-000000000001';

export default function App() {
  const [companies, setCompanies] = useState([]);
  const [selectedCompanyId, setSelectedCompanyId] = useState(DEFAULT_COMPANY_ID);
  const [capTable, setCapTable] = useState(null);
  const [simulationResult, setSimulationResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [simulating, setSimulating] = useState(false);
  const [error, setError] = useState(null);

  // Load companies & initial cap table
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
      await loadCapTable(targetId);
    } catch (err) {
      setError(err.message || 'Failed to connect to CapTableX API backend.');
    } finally {
      setLoading(false);
    }
  };

  const loadCapTable = async (companyId) => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchCapTable(companyId);
      setCapTable(data);
    } catch (err) {
      setError(err.message || 'Error fetching cap table.');
    } finally {
      setLoading(false);
    }
  };

  const handleSimulate = async (formValues) => {
    setSimulating(true);
    setError(null);
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

  return (
    <div className="app-layout">
      <CompanyHeader
        company={capTable}
        onRefresh={() => loadCapTable(selectedCompanyId)}
        loading={loading}
      />

      <main className="main-content">
        {error && (
          <div className="global-error-banner">
            <span>{error}</span>
            <button onClick={() => setError(null)} className="btn-text">Dismiss</button>
          </div>
        )}

        {/* Cap Table Metric Overview Cards */}
        <CapTableSummary capTable={capTable} />

        {/* Two-column layout: Current Cap Table & Round Simulator Form */}
        <div className="layout-grid">
          <div className="grid-left">
            <CapTableTable
              stakeholders={capTable ? capTable.stakeholders : []}
              totalShares={capTable ? capTable.totalShares : 0}
            />
          </div>

          <div className="grid-right">
            <RoundSimulatorForm
              currentValuation={capTable ? capTable.valuation : null}
              onSimulate={handleSimulate}
              loading={simulating}
            />
          </div>
        </div>

        {/* Simulation Output Section */}
        {simulationResult && (
          <section className="simulation-section">
            <SimulationResults result={simulationResult} />
            <OwnershipComparisonChart result={simulationResult} />
          </section>
        )}
      </main>

      <footer className="footer-container">
        <p>CapTableX – Financial Capitalization &amp; Equity Dilution Engine</p>
        <p className="text-muted">Pure BigDecimal Financial Arithmetic • Read-Only Simulation Engine • Spring Boot &amp; PostgreSQL</p>
      </footer>
    </div>
  );
}

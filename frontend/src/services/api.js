const API_BASE = '/api/v1';

export async function fetchCapTable(companyId) {
  const response = await fetch(`${API_BASE}/cap-table/${companyId}`);
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || `Failed to fetch cap table (HTTP ${response.status})`);
  }
  return response.json();
}

export async function simulateFundingRound(payload) {
  const response = await fetch(`${API_BASE}/cap-table/simulate-round`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || `Simulation failed (HTTP ${response.status})`);
  }
  return response.json();
}

export async function executeFundingRound(payload) {
  const response = await fetch(`${API_BASE}/cap-table/execute-round`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || `Transaction execution failed (HTTP ${response.status})`);
  }
  return response.json();
}

export async function fetchTransactionHistory(companyId) {
  const response = await fetch(`${API_BASE}/cap-table/${companyId}/transactions`);
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || `Failed to fetch transaction history (HTTP ${response.status})`);
  }
  return response.json();
}

export async function fetchCompanies() {
  const response = await fetch(`${API_BASE}/companies`);
  if (!response.ok) {
    throw new Error(`Failed to fetch companies (HTTP ${response.status})`);
  }
  return response.json();
}

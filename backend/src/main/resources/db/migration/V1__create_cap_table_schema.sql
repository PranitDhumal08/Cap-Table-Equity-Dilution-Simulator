-- CapTableX Initial Database Schema
-- Version 1: Core Cap Table Structures

-- 1. Company Profile Table
CREATE TABLE IF NOT EXISTS company_profile (
    company_id UUID PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    current_valuation NUMERIC(19, 4) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_company_valuation_positive CHECK (current_valuation > 0)
);

-- 2. Stakeholders Table
CREATE TABLE IF NOT EXISTS stakeholders (
    stakeholder_id UUID PRIMARY KEY,
    company_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stakeholders_company FOREIGN KEY (company_id) REFERENCES company_profile (company_id) ON DELETE CASCADE,
    CONSTRAINT chk_stakeholder_role_valid CHECK (role IN ('FOUNDER', 'VC', 'ANGEL', 'EMPLOYEE'))
);

CREATE INDEX IF NOT EXISTS idx_stakeholders_company_id ON stakeholders (company_id);

-- 3. Cap Table Ledger Table
CREATE TABLE IF NOT EXISTS cap_table_ledger (
    ledger_id UUID PRIMARY KEY,
    stakeholder_id UUID NOT NULL,
    share_class VARCHAR(50) NOT NULL,
    shares_owned NUMERIC(19, 4) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ledger_stakeholder FOREIGN KEY (stakeholder_id) REFERENCES stakeholders (stakeholder_id) ON DELETE CASCADE,
    CONSTRAINT chk_share_class_valid CHECK (share_class IN ('COMMON', 'PREFERRED')),
    CONSTRAINT chk_shares_owned_positive CHECK (shares_owned > 0)
);

CREATE INDEX IF NOT EXISTS idx_ledger_stakeholder_id ON cap_table_ledger (stakeholder_id);

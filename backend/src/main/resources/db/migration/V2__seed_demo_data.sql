-- CapTableX Realistic Seed Data
-- Version 2: NovaFin Technologies Cap Table Demo

-- 1. Insert NovaFin Technologies Company Profile
-- Pre-money current valuation: 40,000,000.0000 (INR)
INSERT INTO company_profile (company_id, company_name, current_valuation, created_at, updated_at)
VALUES (
    'a1b2c3d4-0001-4000-8000-000000000001',
    'NovaFin Technologies',
    40000000.0000,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 2. Insert Stakeholders
-- Founder A (60%), Founder B (30%), Employee ESOP Pool (10%)
INSERT INTO stakeholders (stakeholder_id, company_id, name, role, created_at, updated_at)
VALUES 
    ('a1b2c3d4-0002-4000-8000-000000000002', 'a1b2c3d4-0001-4000-8000-000000000001', 'Founder A', 'FOUNDER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a1b2c3d4-0003-4000-8000-000000000003', 'a1b2c3d4-0001-4000-8000-000000000001', 'Founder B', 'FOUNDER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a1b2c3d4-0004-4000-8000-000000000004', 'a1b2c3d4-0001-4000-8000-000000000001', 'Employee ESOP Pool', 'EMPLOYEE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. Insert Initial Cap Table Ledger Allocations
-- Total Shares: 1,000,000 Common Shares
INSERT INTO cap_table_ledger (ledger_id, stakeholder_id, share_class, shares_owned, created_at, updated_at)
VALUES 
    ('a1b2c3d4-0005-4000-8000-000000000005', 'a1b2c3d4-0002-4000-8000-000000000002', 'COMMON', 600000.0000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a1b2c3d4-0006-4000-8000-000000000006', 'a1b2c3d4-0003-4000-8000-000000000003', 'COMMON', 300000.0000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a1b2c3d4-0007-4000-8000-000000000007', 'a1b2c3d4-0004-4000-8000-000000000004', 'COMMON', 100000.0000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

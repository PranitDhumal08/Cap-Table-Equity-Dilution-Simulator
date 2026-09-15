-- CapTableX Funding Round Transaction History
-- Version 3: Persistent Funding Round Audit Table

CREATE TABLE IF NOT EXISTS funding_round_transactions (
    transaction_id UUID PRIMARY KEY,
    company_id UUID NOT NULL,
    round_name VARCHAR(100) NOT NULL,
    investor_name VARCHAR(255) NOT NULL,
    investor_role VARCHAR(50) NOT NULL,
    share_class VARCHAR(50) NOT NULL,
    pre_money_valuation NUMERIC(19, 4) NOT NULL,
    investment_amount NUMERIC(19, 4) NOT NULL,
    post_money_valuation NUMERIC(19, 4) NOT NULL,
    price_per_share NUMERIC(19, 4) NOT NULL,
    shares_issued NUMERIC(19, 4) NOT NULL,
    investor_ownership_pct NUMERIC(8, 4) NOT NULL,
    executed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_company FOREIGN KEY (company_id) REFERENCES company_profile (company_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_transactions_company_id ON funding_round_transactions (company_id);

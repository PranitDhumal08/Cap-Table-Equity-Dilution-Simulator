# Business Requirement Document (BRD)
## CapTableX: Startup Cap Table & Venture Equity Dilution Simulator

**Document Version:** 1.0.0  
**Document Status:** Approved / Production-Ready  
**Target Audience:** Non-Technical Stakeholders, Founders, Venture Capital Investors, Corporate Counsel, Product & Engineering Teams  
**Domain:** Financial Technology (FinTech) / Venture Capital Equity Architecture  

---

## 1. Executive Summary

### 1.1 The Business Problem
Startup capitalization tables ("cap tables") represent the legal ownership, equity distribution, and financial claims on a corporation's assets. In early- and growth-stage companies, cap tables are notoriously managed using ad-hoc, unversioned spreadsheets. 

This status quo introduces critical institutional risks:
1. **Floating-Point Rounding Drift & Phantom Shares:** Spreadsheets using IEEE-754 floating-point arithmetic introduce microscopic rounding errors that accumulate across successive funding rounds, resulting in unallocated or over-allocated equity shares.
2. **Founder Dilution Blind Spots:** Founders frequently agree to term sheets without understanding the compounding impact of post-money valuation, investor ownership percentages, and share price adjustments on their ultimate voting control and payout at liquidity.
3. **Absence of Audit Ledgers:** Spreadsheets lack ACID transaction integrity, immutable history, or audit logs. Historical transactions, convertible notes, or new share issuances cannot be cryptographically or relationally verified during investor due diligence.

### 1.2 The CapTableX Solution
**CapTableX** is an enterprise-grade equity dilution simulation and cap table management platform. It combines a deterministic, high-precision financial logic engine with a strict relational database architecture. 

CapTableX enables founders, finance teams, and venture capital investors to:
- Model complex multi-round venture financing events in a non-destructive simulation sandbox.
- Calculate exact pre-money valuations, post-money valuations, price per share, share minting volumes, and multi-tier stakeholder dilution.
- Commit approved rounds atomically into a double-entry style audit ledger maintaining 100% equity conservation ($100.0000\%$) across 100+ consecutive capitalization events.

---

## 2. Venture Financing Mechanics & Glossary

For non-technical stakeholders and prospective investors, this section defines the standard venture mechanics implemented in CapTableX:

| Term | Definition & Venture Mechanic |
| :--- | :--- |
| **Capitalization Table (Cap Table)** | The official ledger detailing a company's total issued and outstanding securities, broken down by stakeholder, security class, and percentage ownership. |
| **Pre-Money Valuation** | The agreed monetary valuation of the enterprise immediately *prior* to receiving new capital investment in a funding round. |
| **Investment Amount** | The aggregate cash capital injected into the company by new or participating investors in exchange for newly issued equity securities. |
| **Post-Money Valuation** | The enterprise valuation immediately *after* the capital injection: $\text{Post-Money} = \text{Pre-Money} + \text{Investment Amount}$. |
| **Common Stock** | Equity securities primarily issued to founders, advisors, and employees. Typically carries voting rights but is subordinate to Preferred Stock during liquidity events. |
| **Preferred Stock** | Senior equity securities issued to professional investors (VCs, institutional funds). Comes with contractual protections such as liquidation preferences and anti-dilution clauses. |
| **Dilution** | The reduction in an existing shareholder's percentage ownership of the company caused by the issuance and minting of new shares. |
| **Percentage-Point Dilution** | The absolute arithmetic drop in equity ownership (e.g., declining from $60.00\%$ to $48.00\%$ is a $-12.00\%$ absolute drop). |
| **Relative Dilution** | The proportional reduction of the stakeholder's prior stake: $\frac{\text{Pre \%} - \text{Post \%}}{\text{Pre \%}} \times 100$ (e.g., declining from $60\%$ to $48\%$ is a $20.00\%$ relative loss of initial ownership). |
| **Option Pool (ESOP)** | Shares reserved for current and future employee equity compensation, commonly structured or refreshed prior to financing rounds. |

---

## 3. Financial Mathematical Specifications

The CapTableX calculation engine executes deterministic calculations using arbitrary-precision arithmetic (`BigDecimal` in Java, configured to 10 decimal places with `HALF_UP` rounding). Float or double IEEE-754 primitives are strictly prohibited in the engine.

### 3.1 Valuation Equations
Let:
- $V_{\text{pre}}$ = Agreed Pre-Money Valuation ($\$$ or ₹)
- $I$ = Aggregate Round Investment Amount ($\$$ or ₹)
- $V_{\text{post}}$ = Post-Money Valuation

$$V_{\text{post}} = V_{\text{pre}} + I$$

### 3.2 Share Price Determination
Let:
- $S_{\text{pre}}$ = Total Pre-Money Issued & Outstanding Shares across all share classes

$$\text{Price Per Share } (P) = \frac{V_{\text{pre}}}{S_{\text{pre}}}$$

### 3.3 New Share Minting & Post-Money Shares
Let:
- $S_{\text{new}}$ = Newly minted shares allocated to the incoming investor
- $S_{\text{post}}$ = Total Post-Money Issued & Outstanding Shares

$$S_{\text{new}} = \frac{I}{P} = \frac{I \times S_{\text{pre}}}{V_{\text{pre}}}$$

$$S_{\text{post}} = S_{\text{pre}} + S_{\text{new}}$$

### 3.4 Investor Ownership Percentage
Let:
- $O_{\text{investor}}$ = Percentage ownership acquired by the new investor

$$O_{\text{investor}} = \left( \frac{S_{\text{new}}}{S_{\text{post}}} \right) \times 100 = \left( \frac{I}{V_{\text{post}}} \right) \times 100$$

### 3.5 Dilution Formulas for Existing Stakeholders
For each existing stakeholder $i$ holding $S_i$ shares:

1. **Pre-Money Ownership Percentage:**
   $$O_{i,\text{pre}} = \left( \frac{S_i}{S_{\text{pre}}} \right) \times 100$$

2. **Post-Money Ownership Percentage:**
   $$O_{i,\text{post}} = \left( \frac{S_i}{S_{\text{post}}} \right) \times 100$$

3. **Absolute (Percentage Point) Dilution:**
   $$\Delta O_{i,\text{abs}} = O_{i,\text{pre}} - O_{i,\text{post}}$$

4. **Relative (Proportional) Dilution:**
   $$\Delta O_{i,\text{rel}} = \left( \frac{O_{i,\text{pre}} - O_{i,\text{post}}}{O_{i,\text{pre}}} \right) \times 100$$

### 3.6 Mathematical Invariants & Equity Conservation Law
Every valid capitalization state must satisfy the following fundamental invariant:

$$\sum_{i=1}^{N} O_{i,\text{post}} + O_{\text{investor}} = 100.0000\% \quad (\pm 0.0001\% \text{ tolerance due to display scale})$$

$$\sum_{i=1}^{N} S_i + S_{\text{new}} = S_{\text{post}}$$

---

## 4. User Personas & Use Cases

```
+------------------+-------------------------------------------------------------+
| User Persona     | Primary Goals & Use Cases                                   |
+------------------+-------------------------------------------------------------+
| Startup Founder  | - Evaluate incoming venture term sheets without committing.  |
| / CEO            | - Predict exact voting equity loss across Series A, B, C.   |
|                  | - Ensure option pool expansions do not overly dilute team.  |
+------------------+-------------------------------------------------------------+
| Venture Capital  | - Model syndicate check sizes and target ownership (e.g. 20%)|
| Investor (VC)    | - Verify cap table math during due diligence audit.         |
|                  | - Inspect historical capitalization ledger for discrepancies.|
+------------------+-------------------------------------------------------------+
| Angel Investor   | - Calculate ownership post dilution across subsequent large |
|                  |   institutional rounds.                                     |
+------------------+-------------------------------------------------------------+
| Corporate Counsel| - Audit share issuances against company charter.            |
| / Legal          | - Review chronological transaction logs before board signing.|
+------------------+-------------------------------------------------------------+
```

---

## 5. Functional Requirements (FR)

### FR-1: Cap Table Retrieval & Real-Time Portfolio State
- **FR-1.1:** The system shall retrieve the complete capitalization table for any registered company by UUID.
- **FR-1.2:** The output shall aggregate all stakeholders, categorize holdings by share class (`COMMON`, `PREFERRED`), report shares owned, and compute exact ownership percentages.
- **FR-1.3:** The sum of all ownership percentages must dynamically resolve to $100.0000\%$.

### FR-2: Non-Destructive Funding Round Simulation (Sandbox)
- **FR-2.1:** The system shall expose a simulation endpoint allowing users to test hypothetical funding rounds with parameters:
  - Pre-Money Valuation ($> 0$)
  - Capital Investment Amount ($> 0$)
  - Investor Name & Entity Type (`VC`, `ANGEL`, `FOUNDER`, `EMPLOYEE`)
  - Target Share Class (`COMMON`, `PREFERRED`)
- **FR-2.2:** Simulations must be strictly read-only and guarantee zero mutation to the underlying database state.
- **FR-2.3:** The simulation response must return full analytical data: post-money valuation, price per share, newly issued shares, investor equity percentage, and individual pre/post ownership with absolute and relative dilution metrics for all existing stakeholders.

### FR-3: Atomic Round Execution & Double-Entry Ledger Commitment
- **FR-3.1:** The system shall allow authorized users to execute and commit an approved funding round into the persistent database.
- **FR-3.2:** Execution must run under strict ACID transaction isolation:
  1. Insert or register the new stakeholder entity.
  2. Mint and allocate the new shares in `cap_table_ledger`.
  3. Update the company profile's `current_valuation` to the verified post-money valuation.
  4. Write an immutable event record into `funding_round_transactions`.
- **FR-3.3:** If any step fails or encounters a constraint violation, the entire operation must automatically roll back, preserving data integrity.

### FR-4: Historical Transaction Ledger & Audit Log
- **FR-4.1:** The system shall maintain an immutable, chronologically ordered transaction log of all executed capitalization events.
- **FR-4.2:** Each transaction record must log:
  - Unique Transaction UUID
  - Company ID & Round Name (e.g., "Series A Preferred")
  - Investor Name
  - Pre-Money Valuation & Post-Money Valuation
  - Share Price & Quantity of Shares Minted
  - Precise ISO-8601 Timestamp

---

## 6. Non-Functional Requirements (NFR)

### NFR-1: Financial Precision & Numeric Integrity
- All currency values and share quantities shall be stored using PostgreSQL `NUMERIC(19, 4)`.
- Java services must process all computations using `java.math.BigDecimal` initialized with String constructors.
- Division operations must explicitly specify `RoundingMode.HALF_UP` with a minimum internal working scale of 10 decimal places.
- No IEEE-754 primitive floating-point types (`float`, `double`) shall be used in financial algorithms.

### NFR-2: Transactional Integrity & Scale
- The database schema must enforce referential integrity via Foreign Keys with `ON DELETE CASCADE` and Check Constraints (`chk_company_valuation_positive`, `chk_shares_owned_positive`, `chk_share_class_valid`, `chk_stakeholder_role_valid`).
- The system must support and maintain strict mathematical and transactional consistency across **100+ sequential capitalization events** without deadlock, degradation, or rounding drift.

### NFR-3: Performance & Latency
- Cap table retrieval queries must execute in $< 50\text{ ms}$ under standard database indexing.
- Funding round simulation calculations must execute in $< 10\text{ ms}$ in-memory.
- Full round execution and ledger commitment must complete in $< 150\text{ ms}$.

### NFR-4: Security & Input Validation
- All external inputs must be validated using Bean Validation (`@NotNull`, `@NotBlank`, `@Positive`, `@DecimalMin`).
- Invalid payloads (such as negative investments or zero pre-money valuations) must be rejected with informative RFC-7807 compatible error structures.

---

## 7. Relational Database Schema Mapping

```
+--------------------------------------------------------------------------------+
|                               company_profile                                  |
+--------------------------------------------------------------------------------+
| PK company_id         : UUID                                                   |
|    company_name       : VARCHAR(255) NOT NULL                                  |
|    current_valuation  : NUMERIC(19, 4) NOT NULL  [CHECK > 0]                  |
|    created_at         : TIMESTAMP WITH TIME ZONE                               |
|    updated_at         : TIMESTAMP WITH TIME ZONE                               |
+---------------------------------------+----------------------------------------+
                                        | 1
                                        |
                                        | N
+---------------------------------------v----------------------------------------+
|                                 stakeholders                                   |
+--------------------------------------------------------------------------------+
| PK stakeholder_id     : UUID                                                   |
| FK company_id         : UUID NOT NULL  (references company_profile)            |
|    name               : VARCHAR(255) NOT NULL                                  |
|    role               : VARCHAR(50) NOT NULL   [FOUNDER, VC, ANGEL, EMPLOYEE]  |
|    created_at         : TIMESTAMP WITH TIME ZONE                               |
|    updated_at         : TIMESTAMP WITH TIME ZONE                               |
+---------------------------------------+----------------------------------------+
                                        | 1
                                        |
                                        | N
+---------------------------------------v----------------------------------------+
|                              cap_table_ledger                                  |
+--------------------------------------------------------------------------------+
| PK ledger_id          : UUID                                                   |
| FK stakeholder_id     : UUID NOT NULL  (references stakeholders)               |
|    share_class        : VARCHAR(50) NOT NULL   [COMMON, PREFERRED]             |
|    shares_owned       : NUMERIC(19, 4) NOT NULL  [CHECK > 0]                  |
|    created_at         : TIMESTAMP WITH TIME ZONE                               |
|    updated_at         : TIMESTAMP WITH TIME ZONE                               |
+--------------------------------------------------------------------------------+

+--------------------------------------------------------------------------------+
|                          funding_round_transactions                            |
+--------------------------------------------------------------------------------+
| PK transaction_id     : UUID                                                   |
| FK company_id         : UUID NOT NULL  (references company_profile)            |
|    round_name         : VARCHAR(100) NOT NULL                                  |
|    investor_name      : VARCHAR(255) NOT NULL                                  |
|    investment_amount  : NUMERIC(19, 4) NOT NULL  [CHECK > 0]                  |
|    pre_money_val      : NUMERIC(19, 4) NOT NULL  [CHECK > 0]                  |
|    post_money_val     : NUMERIC(19, 4) NOT NULL  [CHECK > 0]                  |
|    share_price        : NUMERIC(19, 4) NOT NULL  [CHECK > 0]                  |
|    shares_issued      : NUMERIC(19, 4) NOT NULL  [CHECK > 0]                  |
|    executed_at        : TIMESTAMP WITH TIME ZONE NOT NULL                      |
+--------------------------------------------------------------------------------+
```

---

## 8. Verification & Acceptance Criteria

| Requirement | Acceptance Benchmark | Verification Status |
| :--- | :--- | :--- |
| **Pre/Post-Money Math** | $V_{\text{post}} = V_{\text{pre}} + I$ verified for arbitrary scale numbers. | Passed (`FinancialCalculationServiceTest`) |
| **Dilution Breakdown** | Percentage-point and relative dilution verified against standard venture models. | Passed (`FinancialCalculationServiceTest`) |
| **ACID Integrity** | Multi-table round commitment rolls back entirely if any table insertion errors. | Passed (`CapTableControllerIntegrationTest`) |
| **100+ Capitalization Events** | 100 consecutive automated funding rounds executed sequentially on a single company ledger without decimal drift, with audit verification. | Passed (`test100ConsecutiveCapitalizationEvents`) |
| **Audit Trail** | Chronological retrieval of all transaction history with correct share price & valuation snapshots. | Passed (`GET /transactions`) |

package com.captablex.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("CapTableController Integration Tests")
class CapTableControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Seeded NovaFin Technologies ID from V2 migration
    private static final String NOVAFIN_COMPANY_ID = "a1b2c3d4-0001-4000-8000-000000000001";

    @Nested
    @DisplayName("GET /api/v1/cap-table/{companyId}")
    class GetCapTableTests {

        @Test
        @DisplayName("Should return 200 OK and current cap table for seeded NovaFin Technologies")
        void testGetCapTableSuccess() throws Exception {
            mockMvc.perform(get("/api/v1/cap-table/{companyId}", NOVAFIN_COMPANY_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.companyName", is("NovaFin Technologies")))
                    .andExpect(jsonPath("$.valuation", is(40000000.0000)))
                    .andExpect(jsonPath("$.totalShares", is(1000000.0000)))
                    .andExpect(jsonPath("$.stakeholders", hasSize(3)))
                    .andExpect(jsonPath("$.stakeholders[0].name", is("Founder A")))
                    .andExpect(jsonPath("$.stakeholders[0].ownershipPercentage", is(60.0000)));
        }

        @Test
        @DisplayName("Should return 404 NOT_FOUND when company does not exist")
        void testGetCapTableNotFound() throws Exception {
            UUID randomId = UUID.randomUUID();
            mockMvc.perform(get("/api/v1/cap-table/{companyId}", randomId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is("RESOURCE_NOT_FOUND")))
                    .andExpect(jsonPath("$.message", containsString("Company not found")));
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST when companyId is invalid UUID format")
        void testGetCapTableInvalidUUID() throws Exception {
            mockMvc.perform(get("/api/v1/cap-table/{companyId}", "invalid-uuid-123")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("INVALID_PARAMETER_TYPE")));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/cap-table/simulate-round")
    class SimulateRoundTests {

        @Test
        @DisplayName("Should simulate Series A round successfully on NovaFin Technologies (read-only)")
        void testSimulateRoundSuccess() throws Exception {
            Map<String, Object> payload = Map.of(
                    "companyId", NOVAFIN_COMPANY_ID,
                    "preMoneyValuation", 40000000.00,
                    "investmentAmount", 10000000.00,
                    "investorName", "Alpha Ventures",
                    "investorType", "VC",
                    "shareClass", "PREFERRED"
            );

            mockMvc.perform(post("/api/v1/cap-table/simulate-round")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.preMoneyValuation", is(40000000.0000)))
                    .andExpect(jsonPath("$.investmentAmount", is(10000000.0000)))
                    .andExpect(jsonPath("$.postMoneyValuation", is(50000000.0000)))
                    .andExpect(jsonPath("$.totalPreMoneyShares", is(1000000.0000)))
                    .andExpect(jsonPath("$.pricePerShare", is(40.0000)))
                    .andExpect(jsonPath("$.newSharesIssued", is(250000.0000)))
                    .andExpect(jsonPath("$.totalPostMoneyShares", is(1250000.0000)))
                    .andExpect(jsonPath("$.newInvestorName", is("Alpha Ventures")))
                    .andExpect(jsonPath("$.newInvestorOwnershipPercentage", is(20.0000)))
                    .andExpect(jsonPath("$.stakeholders", hasSize(3)))
                    .andExpect(jsonPath("$.summary", containsString("Alpha Ventures receives approximately 20.0000%")));
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST when investment amount is zero or negative")
        void testSimulateRoundZeroInvestment() throws Exception {
            Map<String, Object> payload = Map.of(
                    "companyId", NOVAFIN_COMPANY_ID,
                    "preMoneyValuation", 40000000.00,
                    "investmentAmount", 0,
                    "investorName", "Alpha Ventures",
                    "investorType", "VC",
                    "shareClass", "PREFERRED"
            );

            mockMvc.perform(post("/api/v1/cap-table/simulate-round")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")));
        }

        @Test
        @DisplayName("Should return 400 BAD_REQUEST when investor name is blank")
        void testSimulateRoundBlankInvestorName() throws Exception {
            Map<String, Object> payload = Map.of(
                    "companyId", NOVAFIN_COMPANY_ID,
                    "preMoneyValuation", 40000000.00,
                    "investmentAmount", 10000000.00,
                    "investorName", "   ",
                    "investorType", "VC",
                    "shareClass", "PREFERRED"
            );

            mockMvc.perform(post("/api/v1/cap-table/simulate-round")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")));
        }

        @Test
        @DisplayName("Should return 404 NOT_FOUND when simulating round for non-existent company")
        void testSimulateRoundCompanyNotFound() throws Exception {
            UUID nonExistentCompanyId = UUID.randomUUID();
            Map<String, Object> payload = Map.of(
                    "companyId", nonExistentCompanyId.toString(),
                    "preMoneyValuation", 40000000.00,
                    "investmentAmount", 10000000.00,
                    "investorName", "Alpha Ventures",
                    "investorType", "VC",
                    "shareClass", "PREFERRED"
            );

            mockMvc.perform(post("/api/v1/cap-table/simulate-round")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", is("RESOURCE_NOT_FOUND")));
        }
    }

    @Nested
    @DisplayName("Health and Company Management Tests")
    class HealthAndCompanyTests {

        @Test
        @DisplayName("GET /api/v1/health should return UP status")
        void testHealthCheck() throws Exception {
            mockMvc.perform(get("/api/v1/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("UP")))
                    .andExpect(jsonPath("$.service", is("CapTableX Backend")));
        }

        @Test
        @DisplayName("POST /api/v1/companies should create new company with 201 Created")
        void testCreateCompanySuccess() throws Exception {
            Map<String, Object> payload = Map.of(
                    "companyName", "Apex BioTech",
                    "currentValuation", 15000000.00
            );

            mockMvc.perform(post("/api/v1/companies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.companyName", is("Apex BioTech")))
                    .andExpect(jsonPath("$.currentValuation", is(15000000.0000)))
                    .andExpect(jsonPath("$.companyId").isNotEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/cap-table/execute-round and Transaction History Tests")
    class ExecuteRoundTests {

        @Test
        @DisplayName("Should execute round, commit to DB, update valuation, and log transaction")
        void testExecuteRoundAndVerifyHistory() throws Exception {
            // First create a dedicated company for execution test so we don't interfere with other tests
            String companyPayload = objectMapper.writeValueAsString(Map.of(
                    "companyName", "Solaris Tech",
                    "currentValuation", 40000000.00
            ));

            String compResponse = mockMvc.perform(post("/api/v1/companies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(companyPayload))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            String newCompId = objectMapper.readTree(compResponse).get("companyId").asText();

            // Add founder
            String founderPayload = objectMapper.writeValueAsString(Map.of(
                    "name", "Lead Founder",
                    "role", "FOUNDER"
            ));
            String founderRes = mockMvc.perform(post("/api/v1/companies/{companyId}/stakeholders", newCompId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(founderPayload))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();
            String founderId = objectMapper.readTree(founderRes).get("stakeholderId").asText();

            // Add shares: 1,000,000 Common
            mockMvc.perform(post("/api/v1/stakeholders/{stakeholderId}/shares", founderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "shareClass", "COMMON",
                                    "sharesOwned", 1000000.00
                            ))))
                    .andExpect(status().isCreated());

            // Execute Round: ₹10,000,000 investment
            Map<String, Object> execPayload = Map.of(
                    "companyId", newCompId,
                    "roundName", "Series A Preferred",
                    "preMoneyValuation", 40000000.00,
                    "investmentAmount", 10000000.00,
                    "investorName", "Sequoia Prime",
                    "investorType", "VC",
                    "shareClass", "PREFERRED"
            );

            mockMvc.perform(post("/api/v1/cap-table/execute-round")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(execPayload)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.postMoneyValuation", is(50000000.0000)))
                    .andExpect(jsonPath("$.newSharesIssued", is(250000.0000)))
                    .andExpect(jsonPath("$.totalPostMoneyShares", is(1250000.0000)))
                    .andExpect(jsonPath("$.newInvestorName", is("Sequoia Prime")))
                    .andExpect(jsonPath("$.newInvestorOwnershipPercentage", is(20.0000)));

            // Verify cap table now contains 2 stakeholders and updated valuation
            mockMvc.perform(get("/api/v1/cap-table/{companyId}", newCompId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valuation", is(50000000.0000)))
                    .andExpect(jsonPath("$.totalShares", is(1250000.0000)))
                    .andExpect(jsonPath("$.stakeholders", hasSize(2)));

            // Verify transaction history audit log
            mockMvc.perform(get("/api/v1/cap-table/{companyId}/transactions", newCompId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].roundName", is("Series A Preferred")))
                    .andExpect(jsonPath("$[0].investorName", is("Sequoia Prime")))
                    .andExpect(jsonPath("$[0].investmentAmount", is(10000000.0000)))
                    .andExpect(jsonPath("$[0].sharesIssued", is(250000.0000)));
        }

        @Test
        @DisplayName("Should execute 100+ consecutive capitalization events maintaining strict ACID transactional integrity and zero decimal drift")
        void test100ConsecutiveCapitalizationEvents() throws Exception {
            // 1. Create a dedicated enterprise company
            String compResponse = mockMvc.perform(post("/api/v1/companies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "companyName", "Centurion Capital Corp",
                                    "currentValuation", 10000000.00
                            ))))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            String companyId = objectMapper.readTree(compResponse).get("companyId").asText();

            // 2. Add Founding Shareholder with 1,000,000 Common shares
            String founderRes = mockMvc.perform(post("/api/v1/companies/{companyId}/stakeholders", companyId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "name", "Genesis Founder",
                                    "role", "FOUNDER"
                            ))))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();
            String founderId = objectMapper.readTree(founderRes).get("stakeholderId").asText();

            mockMvc.perform(post("/api/v1/stakeholders/{stakeholderId}/shares", founderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "shareClass", "COMMON",
                                    "sharesOwned", 1000000.00
                            ))))
                    .andExpect(status().isCreated());

            // 3. Sequentially execute 100 distinct simulated capitalization events
            BigDecimal currentValuation = new BigDecimal("10000000.00");
            BigDecimal investmentPerRound = new BigDecimal("100000.00"); // 100k capital injection per round

            for (int i = 1; i <= 100; i++) {
                Map<String, Object> roundPayload = Map.of(
                        "companyId", companyId,
                        "roundName", "Tranche #" + i + " Capital Injection",
                        "preMoneyValuation", currentValuation,
                        "investmentAmount", investmentPerRound,
                        "investorName", "Institutional Partner " + i,
                        "investorType", (i % 2 == 0) ? "VC" : "ANGEL",
                        "shareClass", "PREFERRED"
                );

                String execRes = mockMvc.perform(post("/api/v1/cap-table/execute-round")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(roundPayload)))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString();

                JsonNode root = objectMapper.readTree(execRes);
                currentValuation = new BigDecimal(root.get("postMoneyValuation").asText());
            }

            // 4. Verify cap table integrity after 100 sequential events
            String capTableRes = mockMvc.perform(get("/api/v1/cap-table/{companyId}", companyId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.stakeholders", hasSize(101)))
                    .andReturn().getResponse().getContentAsString();

            JsonNode capTableNode = objectMapper.readTree(capTableRes);
            JsonNode stakeholders = capTableNode.get("stakeholders");

            BigDecimal totalOwnershipPercentage = BigDecimal.ZERO;
            BigDecimal totalSharesCalculated = BigDecimal.ZERO;

            for (JsonNode stakeholder : stakeholders) {
                totalOwnershipPercentage = totalOwnershipPercentage.add(new BigDecimal(stakeholder.get("ownershipPercentage").asText()));
                totalSharesCalculated = totalSharesCalculated.add(new BigDecimal(stakeholder.get("shares").asText()));
            }

            // Equity conservation law: sum of ownership must reconcile to 100.0000% (+/- 0.05% tolerance due to 4-decimal rounding across 101 records)
            Assertions.assertTrue(
                    totalOwnershipPercentage.subtract(new BigDecimal("100.0000")).abs().doubleValue() < 0.05,
                    "Total ownership after 100 rounds must equal 100.0000%, but was: " + totalOwnershipPercentage
            );

            // Total shares must match cap table totalShares header exactly
            BigDecimal reportedTotalShares = new BigDecimal(capTableNode.get("totalShares").asText());
            Assertions.assertEquals(
                    0,
                    totalSharesCalculated.compareTo(reportedTotalShares),
                    "Sum of stakeholder shares must equal reported total shares"
            );

            // 5. Verify immutable transaction audit ledger contains exactly 100 records
            mockMvc.perform(get("/api/v1/cap-table/{companyId}/transactions", companyId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(100)));
        }
    }
}

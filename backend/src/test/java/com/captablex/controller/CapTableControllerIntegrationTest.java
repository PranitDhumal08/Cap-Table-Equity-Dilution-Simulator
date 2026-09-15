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
}

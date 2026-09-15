package com.captablex.controller;

import com.captablex.dto.request.CreateCompanyRequest;
import com.captablex.dto.response.CompanyResponse;
import com.captablex.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Endpoints for managing company profiles")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @Operation(summary = "Create a company", description = "Registers a new company profile with baseline valuation")
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        CompanyResponse response = companyService.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all companies", description = "Retrieves all registered company profiles")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{companyId}")
    @Operation(summary = "Get company by ID", description = "Retrieves a single company profile by its UUID")
    public ResponseEntity<CompanyResponse> getCompany(@PathVariable UUID companyId) {
        return ResponseEntity.ok(companyService.getCompany(companyId));
    }
}

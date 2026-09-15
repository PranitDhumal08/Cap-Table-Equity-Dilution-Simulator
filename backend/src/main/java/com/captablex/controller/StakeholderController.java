package com.captablex.controller;

import com.captablex.dto.request.AddLedgerEntryRequest;
import com.captablex.dto.request.CreateStakeholderRequest;
import com.captablex.dto.response.LedgerResponse;
import com.captablex.dto.response.StakeholderResponse;
import com.captablex.service.StakeholderService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Stakeholders & Equity", description = "Endpoints for managing stakeholders and cap table ledger allocations")
public class StakeholderController {

    private final StakeholderService stakeholderService;

    @PostMapping("/companies/{companyId}/stakeholders")
    @Operation(summary = "Create stakeholder", description = "Adds a new stakeholder to an existing company")
    public ResponseEntity<StakeholderResponse> createStakeholder(
            @PathVariable UUID companyId,
            @Valid @RequestBody CreateStakeholderRequest request) {
        StakeholderResponse response = stakeholderService.createStakeholder(companyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/companies/{companyId}/stakeholders")
    @Operation(summary = "List stakeholders", description = "Retrieves all stakeholders belonging to a company")
    public ResponseEntity<List<StakeholderResponse>> getStakeholdersByCompany(
            @PathVariable UUID companyId) {
        return ResponseEntity.ok(stakeholderService.getStakeholdersByCompany(companyId));
    }

    @PostMapping("/stakeholders/{stakeholderId}/shares")
    @Operation(summary = "Add cap-table ledger entry", description = "Issues shares of a given class to a stakeholder")
    public ResponseEntity<LedgerResponse> addLedgerEntry(
            @PathVariable UUID stakeholderId,
            @Valid @RequestBody AddLedgerEntryRequest request) {
        LedgerResponse response = stakeholderService.addLedgerEntry(stakeholderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

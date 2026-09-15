package com.captablex.controller;

import com.captablex.dto.request.ExecuteRoundRequest;
import com.captablex.dto.request.SimulateRoundRequest;
import com.captablex.dto.response.CapTableResponse;
import com.captablex.dto.response.SimulateRoundResponse;
import com.captablex.dto.response.TransactionHistoryResponse;
import com.captablex.service.CapTableService;
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
@RequestMapping("/api/v1/cap-table")
@RequiredArgsConstructor
@Tag(name = "Cap Table & Simulation", description = "Endpoints for cap table inspection, round simulation, and transaction execution")
public class CapTableController {

    private final CapTableService capTableService;

    @GetMapping("/{companyId}")
    @Operation(summary = "Get current cap table", description = "Retrieves the full capitalization table and ownership distribution for a company")
    public ResponseEntity<CapTableResponse> getCapTable(@PathVariable UUID companyId) {
        CapTableResponse response = capTableService.getCapTable(companyId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/simulate-round")
    @Operation(summary = "Simulate funding round", description = "Performs an idempotent, read-only venture round dilution calculation without mutating database state")
    public ResponseEntity<SimulateRoundResponse> simulateRound(@Valid @RequestBody SimulateRoundRequest request) {
        SimulateRoundResponse response = capTableService.simulateRound(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/execute-round")
    @Operation(summary = "Execute and commit funding round", description = "Atomically executes the funding round, updates company valuation, issues new shares, and commits a persistent transaction record")
    public ResponseEntity<SimulateRoundResponse> executeRound(@Valid @RequestBody ExecuteRoundRequest request) {
        SimulateRoundResponse response = capTableService.executeRound(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{companyId}/transactions")
    @Operation(summary = "Get transaction history", description = "Retrieves chronological audit log of all executed venture funding rounds for a company")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionHistory(@PathVariable UUID companyId) {
        List<TransactionHistoryResponse> history = capTableService.getTransactionHistory(companyId);
        return ResponseEntity.ok(history);
    }
}

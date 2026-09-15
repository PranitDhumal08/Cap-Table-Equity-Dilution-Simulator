package com.captablex.controller;

import com.captablex.dto.request.SimulateRoundRequest;
import com.captablex.dto.response.CapTableResponse;
import com.captablex.dto.response.SimulateRoundResponse;
import com.captablex.service.CapTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cap-table")
@RequiredArgsConstructor
@Tag(name = "Cap Table & Simulation", description = "Endpoints for cap table inspection and funding round dilution simulation")
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
}

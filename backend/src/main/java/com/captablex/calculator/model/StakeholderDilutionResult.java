package com.captablex.calculator.model;

import com.captablex.domain.ShareClass;
import com.captablex.domain.StakeholderRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Result model representing before-and-after ownership and dilution metrics
 * for an individual existing stakeholder.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StakeholderDilutionResult {
    private UUID stakeholderId;
    private String name;
    private StakeholderRole role;
    private ShareClass shareClass;
    private BigDecimal sharesOwned;
    private BigDecimal previousOwnershipPercentage;
    private BigDecimal newOwnershipPercentage;
    private BigDecimal dilutionPercentagePoints;
    private BigDecimal relativeDilutionPercentage;
}

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
 * Immutable input model capturing an existing stakeholder's current shareholdings.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExistingStakeholderShareInput {
    private UUID stakeholderId;
    private String name;
    private StakeholderRole role;
    private ShareClass shareClass;
    private BigDecimal sharesOwned;
}

package com.captablex.dto.response;

import com.captablex.domain.ShareClass;
import com.captablex.domain.StakeholderRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Represents a stakeholder's current ownership stake and share allocation")
public class StakeholderOwnershipDto {

    private UUID stakeholderId;

    @Schema(description = "Stakeholder name", example = "Founder A")
    private String name;

    @Schema(description = "Role in company", example = "FOUNDER")
    private StakeholderRole role;

    @Schema(description = "Class of shares held", example = "COMMON")
    private ShareClass shareClass;

    @Schema(description = "Number of shares owned", example = "600000.0000")
    private BigDecimal shares;

    @Schema(description = "Current percentage ownership in company", example = "60.0000")
    private BigDecimal ownershipPercentage;
}

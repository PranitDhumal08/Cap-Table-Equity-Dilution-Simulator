package com.captablex.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Complete cap table snapshot of a company")
public class CapTableResponse {

    @Schema(description = "Unique company identifier")
    private UUID companyId;

    @Schema(description = "Company trade name", example = "NovaFin Technologies")
    private String companyName;

    @Schema(description = "Current company baseline valuation", example = "40000000.00")
    private BigDecimal valuation;

    @Schema(description = "Total issued and outstanding shares", example = "1000000.0000")
    private BigDecimal totalShares;

    @Builder.Default
    @Schema(description = "Breakdown of individual stakeholder shareholdings")
    private List<StakeholderOwnershipDto> stakeholders = new ArrayList<>();
}

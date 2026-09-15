package com.captablex.dto.request;

import com.captablex.domain.ShareClass;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for issuing shares to an existing stakeholder")
public class AddLedgerEntryRequest {

    @NotNull(message = "Share class is required (COMMON, PREFERRED)")
    @Schema(description = "Class of shares issued", example = "COMMON")
    private ShareClass shareClass;

    @NotNull(message = "Shares owned count is required")
    @DecimalMin(value = "0.0001", message = "Shares owned must be greater than zero")
    @Schema(description = "Quantity of shares allocated", example = "600000.0000")
    private BigDecimal sharesOwned;
}

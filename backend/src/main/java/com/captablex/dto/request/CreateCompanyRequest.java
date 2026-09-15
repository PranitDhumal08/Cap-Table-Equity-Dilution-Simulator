package com.captablex.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Request body for registering a new startup entity")
public class CreateCompanyRequest {

    @NotBlank(message = "Company name cannot be blank")
    @Schema(description = "Legal trade name of the startup", example = "NovaFin Technologies")
    private String companyName;

    @NotNull(message = "Current valuation is required")
    @DecimalMin(value = "0.0001", message = "Current valuation must be greater than zero")
    @Schema(description = "Current baseline company valuation", example = "40000000.00")
    private BigDecimal currentValuation;
}

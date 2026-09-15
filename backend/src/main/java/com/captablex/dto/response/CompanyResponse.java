package com.captablex.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response representation of a company profile")
public class CompanyResponse {
    private UUID companyId;
    private String companyName;
    private BigDecimal currentValuation;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

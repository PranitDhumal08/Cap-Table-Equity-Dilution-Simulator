package com.captablex.dto.response;

import com.captablex.domain.ShareClass;
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
@Schema(description = "Response representation of a cap table ledger share issuance")
public class LedgerResponse {
    private UUID ledgerId;
    private UUID stakeholderId;
    private ShareClass shareClass;
    private BigDecimal sharesOwned;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

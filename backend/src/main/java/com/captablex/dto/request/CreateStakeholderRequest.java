package com.captablex.dto.request;

import com.captablex.domain.StakeholderRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for onboarding a new stakeholder")
public class CreateStakeholderRequest {

    @NotBlank(message = "Stakeholder name cannot be blank")
    @Schema(description = "Name of individual or institutional entity", example = "Founder A")
    private String name;

    @NotNull(message = "Stakeholder role is required (FOUNDER, VC, ANGEL, EMPLOYEE)")
    @Schema(description = "Role category of stakeholder", example = "FOUNDER")
    private StakeholderRole role;
}

package com.captablex.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standardized error response envelope")
public class ErrorResponse {

    @Schema(description = "ISO-8601 timestamp of error occurrence")
    private OffsetDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "High-level error classification", example = "VALIDATION_ERROR")
    private String error;

    @Schema(description = "Descriptive error message", example = "Investment amount must be greater than zero")
    private String message;

    @Schema(description = "API endpoint path where error occurred", example = "/api/v1/cap-table/simulate-round")
    private String path;

    @Schema(description = "Detailed list of field validation errors, if applicable")
    private List<String> validationErrors;
}

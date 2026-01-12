package com.mjc.school.service.dto;

import com.mjc.school.service.validator.constraint.NotNull;
import com.mjc.school.service.validator.constraint.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description="Request object for creating or updating a tag")
public record TagDtoRequest(
    @Schema(description="Tag name", example="Technology", minLength=3, maxLength=15)
    @NotNull
    @Size(min = 3, max = 15)
    String name) {
}

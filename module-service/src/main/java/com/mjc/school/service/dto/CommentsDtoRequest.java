package com.mjc.school.service.dto;

import com.mjc.school.service.validator.constraint.NotNull;
import com.mjc.school.service.validator.constraint.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description="Request object for creating or updating a comment")
public record CommentsDtoRequest(
        @Schema(description="Comment content", example="this is a great article!", minLength=5, maxLength=255)
        @NotNull
        @Size(min = 5, max = 255)
        String content,

        @Schema(description="ID of the news article this comment belong to", example="2")
        @NotNull
        Long newsId) {
}

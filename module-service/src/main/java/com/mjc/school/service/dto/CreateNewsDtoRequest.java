package com.mjc.school.service.dto;

import com.mjc.school.service.validator.constraint.NotNull;
import com.mjc.school.service.validator.constraint.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description="Request object for creating a news article")
public record CreateNewsDtoRequest(
    @Schema(description="News article title", example="Spring boot",minLength=5,maxLength=30)
    @NotNull
    @Size(min = 5, max = 30)
    String title,

    @Schema(description="News article content", example="Spring Boot makes it easy to create...",minLength=5,maxLength=255)
    @NotNull
    @Size(min = 5, max = 255)
    String content,

    @Schema(description="Author name", example="Rod Johnson")
    @NotNull
    String author,

    @Schema(description="List of tag names", example="[\"Technology\",\"Science\"]")
    List<String> tags,

    @Schema(description="List of comments IDs", example="[1,2,3]")
    List<Long> commentsIds
) {
     public CreateNewsDtoRequest {
         if (tags == null) {
             tags = new ArrayList<>();
         }
         if (commentsIds == null) {
            commentsIds = new ArrayList<>();
         }
     }
}

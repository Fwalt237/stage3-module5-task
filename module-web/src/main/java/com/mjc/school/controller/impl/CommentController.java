package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.assembler.CommentModelAssembler;
import com.mjc.school.controller.assembler.LinkBuilderUtil;
import com.mjc.school.controller.assembler.PageModelAssembler;
import com.mjc.school.exception.handler.ErrorResponse;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.CommentsDtoRequest;
import com.mjc.school.service.dto.CommentsDtoResponse;
import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.dto.ResourceSearchFilterRequestDTO;
import com.mjc.school.versioning.ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.mjc.school.controller.RestApiConst.COMMENTS_API_ROOT_PATH;

@Tag(name = "Comments", description = "handle comments CRUD operations")
@ApiVersion(1)
@RestController
@RequestMapping(value = COMMENTS_API_ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController
    implements BaseController<CommentsDtoRequest, CommentsDtoResponse, Long, ResourceSearchFilterRequestDTO, CommentsDtoRequest> {

    private final BaseService<CommentsDtoRequest, CommentsDtoResponse, Long, ResourceSearchFilterRequestDTO, CommentsDtoRequest> commentsService;
    private final CommentModelAssembler commentAssembler;
    private final PageModelAssembler pageAssembler;
    private final LinkBuilderUtil linkBuilder;

    @Autowired
    public CommentController(
            final BaseService<CommentsDtoRequest, CommentsDtoResponse, Long, ResourceSearchFilterRequestDTO, CommentsDtoRequest> commentsService,
            final CommentModelAssembler commentAssembler,
            final PageModelAssembler pageAssembler,
            final LinkBuilderUtil linkBuilder) {
        this.commentsService = commentsService;
        this.commentAssembler = commentAssembler;
        this.pageAssembler = pageAssembler;
        this.linkBuilder = linkBuilder;
    }



    @Operation(summary = "Get all comments",
            description = "Retrieves a paginated list of all comments with optional filtering and sorting. Supports Hateoas links for navigation.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of comments",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentsDtoResponse.class)
                    )
            )
    })
    @Override
    @GetMapping
    public PageDtoResponse<CommentsDtoResponse> readAll(
            @Parameter(description="Search and filter parameters.")
            final ResourceSearchFilterRequestDTO searchRequest) {
        PageDtoResponse<CommentsDtoResponse> page = commentsService.readAll(searchRequest);

        page.getModelDtoList().forEach(commentAssembler::toModel);

        String baseUrl = linkBuilder.buildCollectionLink(CommentController.class);
        return pageAssembler.addPaginationLinks(page,searchRequest,baseUrl);
    }


    @Operation(summary = "Get comment by ID",
            description = "Retrieves a specific comment by its unique identifier. Uses API version 2.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved a comment",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentsDtoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found with the provided ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Override
    @ApiVersion(2)
    @GetMapping("/{id}")
    public CommentsDtoResponse readById(
            @Parameter(description="Comment ID", required = true, example="2")
            @PathVariable Long id) {
        CommentsDtoResponse comment = commentsService.readById(id);
        return commentAssembler.toModel(comment);
    }



    @Operation(summary = "Create a new comment",
            description = "Creates a new comment associated with a news article")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Comment Successfully created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentsDtoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - validation failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Associated news article not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentsDtoResponse create(
            @Parameter(description="Comment data to create", required = true)
            @RequestBody CommentsDtoRequest dtoRequest) {
        CommentsDtoResponse comment = commentsService.create(dtoRequest);
        return commentAssembler.toModel(comment);
    }


    @Operation(summary = "Update an existing comment",
            description = "Update an existing comment's information by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment Successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentsDtoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - validation failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found with the provided ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Override
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommentsDtoResponse update(
            @Parameter(description="Comment ID", required = true, example="5")
            @PathVariable Long id,
            @Parameter(description="Updated Comment data", required = true)
            @RequestBody CommentsDtoRequest dtoRequest) {
        CommentsDtoResponse comment = commentsService.update(id, dtoRequest);
        return commentAssembler.toModel(comment);
    }



    @Operation(summary = "Delete an existing comment",
            description = "Deletes a comment by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Comment Successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
            @Parameter(description="Comment ID", required = true, example="4")
            @PathVariable Long id) {
        commentsService.deleteById(id);
    }
}

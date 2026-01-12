package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.assembler.AuthorModelAssembler;
import com.mjc.school.controller.assembler.LinkBuilderUtil;
import com.mjc.school.controller.assembler.PageModelAssembler;
import com.mjc.school.exception.handler.ErrorResponse;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
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

import static com.mjc.school.controller.RestApiConst.AUTHOR_API_ROOT_PATH;


@Tag(name = "Authors", description = "handle author CRUD operations")
@ApiVersion(1)
@RestController
@RequestMapping(value = AUTHOR_API_ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthorController
    implements BaseController<AuthorDtoRequest, AuthorDtoResponse, Long, ResourceSearchFilterRequestDTO, AuthorDtoRequest> {

    private final BaseService<AuthorDtoRequest, AuthorDtoResponse, Long, ResourceSearchFilterRequestDTO, AuthorDtoRequest> authorService;
    private final AuthorModelAssembler authorAssembler;
    private final PageModelAssembler pageAssembler;
    private final LinkBuilderUtil linkBuilder;

    @Autowired
    public AuthorController(
        final BaseService<AuthorDtoRequest, AuthorDtoResponse, Long, ResourceSearchFilterRequestDTO, AuthorDtoRequest> authorService
    ,final AuthorModelAssembler authorAssembler,final PageModelAssembler pageAssembler,final LinkBuilderUtil linkBuilder) {
        this.authorService = authorService;
        this.authorAssembler=authorAssembler;
        this.pageAssembler=pageAssembler;
        this.linkBuilder = linkBuilder;
    }

    @Operation(summary = "Get all authors",
    description = "Retrieves a paginated list of all authors with optional filtering and sorting. Supports Hateoas links for navigation.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of authors",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageDtoResponse.class)
                    )
            )
    })
    @Override
    @GetMapping
    public PageDtoResponse<AuthorDtoResponse> readAll(
            @Parameter(description="Search and filter parameters.")
            final ResourceSearchFilterRequestDTO searchRequest) {
        PageDtoResponse<AuthorDtoResponse> page = authorService.readAll(searchRequest);

        page.getModelDtoList().forEach(authorAssembler::toModel);

        String baseUrl = linkBuilder.buildCollectionLink(AuthorController.class);
        return pageAssembler.addPaginationLinks(page,searchRequest,baseUrl);
    }

    @Operation(summary = "Get author by ID",
            description = "Retrieves a specific author by its unique identifier. Uses API version 2.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved author",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthorDtoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Author not found with the provided ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Override
    @ApiVersion(2)
    @GetMapping("/{id}")
    public AuthorDtoResponse readById(
            @Parameter(description="Author ID", required = true, example="1")
            @PathVariable Long id) {
        AuthorDtoResponse author = authorService.readById(id);
        return authorAssembler.toModel(author);
    }


    @Operation(summary = "Create a new author",
            description = "Creates a new author with the provided information")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Author Successfully created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthorDtoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - validation failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDtoResponse create(
            @Parameter(description="Author data to create", required = true)
            @RequestBody AuthorDtoRequest dtoRequest) {
        AuthorDtoResponse author =  authorService.create(dtoRequest);
        return authorAssembler.toModel(author);
    }


    @Operation(summary = "Update an existing author",
            description = "Update an existing author's information by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Author Successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthorDtoResponse.class)
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
                    description = "Author not found with the provided ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Override
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthorDtoResponse update(
            @Parameter(description="Author ID", required = true, example="2")
            @PathVariable Long id,
            @Parameter(description="Updated author data", required = true)
            @RequestBody AuthorDtoRequest dtoRequest) {
        AuthorDtoResponse author =  authorService.update(id, dtoRequest);
        return authorAssembler.toModel(author);
    }


    @Operation(summary = "Delete an existing author",
            description = "Deletes an author by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Author Successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Author not found with the provided ID",
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
            @Parameter(description="Author ID to delete", required = true, example="4")
            @PathVariable Long id) {
        authorService.deleteById(id);
    }
}

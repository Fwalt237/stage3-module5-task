package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.assembler.LinkBuilderUtil;
import com.mjc.school.controller.assembler.PageModelAssembler;
import com.mjc.school.controller.assembler.TagModelAssembler;
import com.mjc.school.exception.handler.ErrorResponse;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.dto.ResourceSearchFilterRequestDTO;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
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

import static com.mjc.school.controller.RestApiConst.TAG_API_ROOT_PATH;


@Tag(name = "Tags", description = "handle tags CRUD operations")
@ApiVersion(1)
@RestController
@RequestMapping(value = TAG_API_ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class TagController
    implements BaseController<TagDtoRequest, TagDtoResponse, Long, ResourceSearchFilterRequestDTO, TagDtoRequest> {

    private final BaseService<TagDtoRequest, TagDtoResponse, Long, ResourceSearchFilterRequestDTO, TagDtoRequest> tagService;
    private final TagModelAssembler tagAssembler;
    private final PageModelAssembler pageAssembler;
    private final LinkBuilderUtil linkBuilder;

    @Autowired
    public TagController(final BaseService<TagDtoRequest, TagDtoResponse, Long, ResourceSearchFilterRequestDTO, TagDtoRequest> tagService,
                         final TagModelAssembler tagAssembler,
                         final PageModelAssembler pageAssembler,
                         final LinkBuilderUtil linkBuilder) {
        this.tagService = tagService;
        this.tagAssembler = tagAssembler;
        this.pageAssembler = pageAssembler;
        this.linkBuilder = linkBuilder;
    }


    @Operation(summary = "Get all tags",
            description = "Retrieves a paginated list of all tags with optional filtering and sorting. Supports Hateoas links for navigation.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of tags",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageDtoResponse.class)
                    )
            )
    })
    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PageDtoResponse<TagDtoResponse> readAll(
            @Parameter(description="Search and filter parameters.")
            ResourceSearchFilterRequestDTO searchRequest) {
        PageDtoResponse<TagDtoResponse> page = tagService.readAll(searchRequest);

        page.getModelDtoList().forEach(tagAssembler::toModel);

        String baseUrl = linkBuilder.buildCollectionLink(TagController.class);
        return pageAssembler.addPaginationLinks(page,searchRequest,baseUrl);
    }

    @Operation(summary = "Get tag by ID",
            description = "Retrieves a specific tag by its unique identifier. Uses API version 2.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved a tag",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TagDtoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tag not found with the provided ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Override
    @ApiVersion(2)
    @GetMapping("/{id}")
    public TagDtoResponse readById(
            @Parameter(description="Tag ID", required = true, example="2")
            @PathVariable Long id) {
        TagDtoResponse tag = tagService.readById(id);
        return tagAssembler.toModel(tag);
    }



    @Operation(summary = "Create a new tag",
            description = "Creates a new tag with the provided name")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tag Successfully created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TagDtoResponse.class)
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
    public TagDtoResponse create(
            @Parameter(description="Tag data to create", required = true)
            @RequestBody TagDtoRequest dtoRequest) {
        TagDtoResponse tag = tagService.create(dtoRequest);
        return tagAssembler.toModel(tag);
    }



    @Operation(summary = "Update an existing Tag",
            description = "Update an existing tag's information by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag Successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TagDtoResponse.class)
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
                    description = "Tag not found with the provided ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Override
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TagDtoResponse update(
            @Parameter(description="Tag ID", required = true, example="8")
            @PathVariable Long id,
            @Parameter(description="Updated tag data", required = true)
            @RequestBody TagDtoRequest dtoRequest) {
        TagDtoResponse tag = tagService.update(id, dtoRequest);
        return tagAssembler.toModel(tag);
    }



    @Operation(summary = "Delete an existing tag",
            description = "Deletes a tag by its unique identifier")
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
            @Parameter(description="Tag ID to delete", required = true, example="7")
            @PathVariable Long id) {
         tagService.deleteById(id);
    }
}

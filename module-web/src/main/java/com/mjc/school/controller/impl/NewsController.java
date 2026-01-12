package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.assembler.*;
import com.mjc.school.exception.handler.ErrorResponse;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.*;
import com.mjc.school.service.impl.AuthorService;
import com.mjc.school.service.impl.CommentService;
import com.mjc.school.service.impl.TagService;
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

import java.util.List;

import static com.mjc.school.controller.RestApiConst.NEWS_API_ROOT_PATH;


@Tag(name = "News", description = "handle author news with authors, tags and comments")
@ApiVersion(1)
@RestController
@RequestMapping(value = NEWS_API_ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class NewsController
    implements BaseController<CreateNewsDtoRequest, NewsDtoResponse, Long, ResourceSearchFilterRequestDTO, UpdateNewsDtoRequest> {

    private final BaseService<CreateNewsDtoRequest, NewsDtoResponse, Long, ResourceSearchFilterRequestDTO, UpdateNewsDtoRequest> newsService;
    private final TagService tagService;
    private final AuthorService authorService;
    private final CommentService commentService;
    private final NewsModelAssembler newsAssembler;
    private final AuthorModelAssembler authorAssembler;
    private final TagModelAssembler tagAssembler;
    private final CommentModelAssembler commentAssembler;
    private final PageModelAssembler pageAssembler;
    private final LinkBuilderUtil linkBuilder;


    @Autowired
    public NewsController(
         final BaseService<CreateNewsDtoRequest, NewsDtoResponse, Long, ResourceSearchFilterRequestDTO, UpdateNewsDtoRequest> newsService,
         final TagService tagService,
         final AuthorService authorService,
         final CommentService commentService,
         final NewsModelAssembler newsAssembler,
         final AuthorModelAssembler authorAssembler,
         final TagModelAssembler tagAssembler,
         final CommentModelAssembler commentAssembler,
         final PageModelAssembler pageAssembler,
         final LinkBuilderUtil linkBuilder) {
        this.newsService = newsService;
        this.tagService = tagService;
        this.authorService = authorService;
        this.commentService = commentService;
        this.newsAssembler = newsAssembler;
        this.authorAssembler = authorAssembler;
        this.tagAssembler = tagAssembler;
        this.commentAssembler = commentAssembler;
        this.pageAssembler = pageAssembler;
        this.linkBuilder = linkBuilder;
    }

    @Operation(summary = "Get all news",
            description = "Retrieves a paginated list of all news articles with optional filtering and sorting. Supports Hateoas links for navigation.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of news",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageDtoResponse.class)
                    )
            )
    })
    @Override
    @GetMapping
    public PageDtoResponse<NewsDtoResponse> readAll(
            @Parameter(description="Search and filter parameters.")
            final ResourceSearchFilterRequestDTO searchRequest) {
        PageDtoResponse<NewsDtoResponse> page = newsService.readAll(searchRequest);

        page.getModelDtoList().forEach(newsAssembler::toModel);

        String baseUrl = linkBuilder.buildCollectionLink(NewsController.class);
        return pageAssembler.addPaginationLinks(page,searchRequest,baseUrl);
    }


    @Operation(summary = "Get news by ID",
            description = "Retrieves a specific news article by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved news article",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NewsDtoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "News article not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Override
    @GetMapping("/{id}")
    public NewsDtoResponse readById(
            @Parameter(description="News article ID", required = true, example="1")
            @PathVariable Long id) {
        NewsDtoResponse news = newsService.readById(id);
        return newsAssembler.toModel(news);
    }



    @Operation(summary = "Creates a news article",
            description = "Creates a news article with title, content, author and tags")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "News article Successfully created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NewsDtoResponse.class)
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
    public NewsDtoResponse create(
            @Parameter(description="News article data to create", required = true)
            @RequestBody CreateNewsDtoRequest dtoRequest) {
        NewsDtoResponse news = newsService.create(dtoRequest);
        return newsAssembler.toModel(news);
    }


    @Operation(summary = "Update an existing news article",
            description = "Update an existing news article by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "News Successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NewsDtoResponse.class)
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
                    description = "News article not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @Override
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public NewsDtoResponse update(
            @Parameter(description="News article ID", required = true, example="1")
            @PathVariable Long id,
            @Parameter(description="Updated news article data", required = true)
            @RequestBody UpdateNewsDtoRequest dtoRequest) {
        NewsDtoResponse news = newsService.update(id, dtoRequest);
        return newsAssembler.toModel(news);
    }


    @Operation(summary = "Delete a news article",
            description = "Delete a news article by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "News article Successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "News article not found",
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
            @Parameter(description="News article ID to delete", required = true, example="5")
            @PathVariable Long id) {
        newsService.deleteById(id);
    }


    @Operation(summary = "Get tags for a news article",
            description = "Retrieves all tags associated with a specific news article.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved tags",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "News article not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}/tags")
    public List<TagDtoResponse> readTagsByNewsId(
            @Parameter(description="News article ID", required = true, example="3")
            @PathVariable Long id) {
        List<TagDtoResponse> tags = tagService.readByNewsId(id);
        tags.forEach(tagAssembler::toModel);
        return tags;
    }


    @Operation(summary = "Get author of a news article",
            description = "Retrieves the author information for a specific news article")
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
                    description = "News article or author not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}/author")
    public AuthorDtoResponse readAuthorByNewsId(
            @Parameter(description="News article ID", required = true, example="3")
            @PathVariable Long id) {
        AuthorDtoResponse author = authorService.readByNewsId(id);
        return authorAssembler.toModel(author);
    }


    @Operation(summary = "Get comments for a news article",
            description = "Retrieves all comments associated with a specific news article.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved comments",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "News article not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}/comments")
    public List<CommentsDtoResponse> readCommentsByNewsId(
            @Parameter(description="News article ID", required = true, example="2")
            @PathVariable Long id) {
        List<CommentsDtoResponse> comments = commentService.readByNewsId(id);
        comments.forEach(commentAssembler::toModel);
        return comments;
    }
}

package org.example.requestservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.TagDto;
import org.example.requestservice.dto.TagRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.services.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "TAG Controller", description = "Управление тегами")
@Controller
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private static final Logger LOGGER = Logger.getLogger(TagController.class);

    private final TagService tagService;

    @Operation(summary = "Добавление тега")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тег добавлен успешно"),
            @ApiResponse(responseCode = "400", description = "Не верный запрос", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = BadRequestException.class)))
            })
    })
    @PostMapping()
    public ResponseEntity<TagDto> addTag(
            @RequestBody TagRequest request
    ) throws BadRequestException {
        LOGGER.info("Запрос на добавление тега");
        return ResponseEntity.ok(tagService.addTag(request));
    }

    @Operation(summary = "Удаление тега",
            description = "Тег нельзя удалить если есть прикрепленные к нему запросы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тег удален успешно"),
            @ApiResponse(responseCode = "400", description = "Не верный запрос, либо тег удалить нельзя", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = BadRequestException.class)))
            }),
            @ApiResponse(responseCode = "404", description = "Тег не найден", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = NotFoundException.class)))
            })
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> deleteTag(
            @PathVariable Long id
    ) throws NotFoundException, BadRequestException {
        LOGGER.info("Запрос на удаление тега id=" + id);
        return ResponseEntity.ok(tagService.removeTag(id));
    }

    @Operation(summary = "Получение тега")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тег получен успешно"),
            @ApiResponse(responseCode = "404", description = "Тег не найден", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = NotFoundException.class)))
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getTag(
            @PathVariable Long id
    ) throws NotFoundException {
        LOGGER.info("Запрос на получение тега id=" + id);
        return ResponseEntity.ok(tagService.getTag(id));
    }

    @Operation(summary = "Получение списка тегов")
    @ApiResponse(responseCode = "200", description = "Список тегов получен")
    @GetMapping()
    public ResponseEntity<List<TagDto>> getTagList() {
        LOGGER.info("Запрос на получение списка тегов");
        return ResponseEntity.ok(tagService.getList());
    }

}

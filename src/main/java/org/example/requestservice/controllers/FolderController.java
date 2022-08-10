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
import org.example.requestservice.dto.FolderDto;
import org.example.requestservice.dto.FolderRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.services.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "FOLDER Controller", description = "Управление папками")
@Controller
@RequiredArgsConstructor
@RequestMapping("/folders")
public class FolderController {

    private static final Logger LOGGER = Logger.getLogger(FolderController.class);
    private final FolderService folderService;

    @Operation(summary = "Добавление папки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Папка добавлена успешно"),
            @ApiResponse(responseCode = "400", description = "Не верный запрос", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = BadRequestException.class)))
            })
    })
    @PostMapping()
    public ResponseEntity<FolderDto> addFolder(
            @RequestBody FolderRequest request
    ) throws BadRequestException {
        LOGGER.info("Запрос на добавление папки");
        return ResponseEntity.ok(folderService.addFolder(request));
    }

    @Operation(summary = "Получение папки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Папка получена успешно"),
            @ApiResponse(responseCode = "404", description = "Папка не найдена", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = NotFoundException.class)))
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<FolderDto> getFolder(
            @PathVariable Long id
    ) throws NotFoundException {
        LOGGER.info("Запрос на получение папки id=" + id);
        return ResponseEntity.ok(folderService.getFolder(id));
    }

    @Operation(summary = "Удаление папки",
            description = "Папку нельзя удалить если есть прикрепленные к ней запросы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Папка удалена успешно"),
            @ApiResponse(responseCode = "400", description = "Не верный запрос, либо папку нельзя удалить", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = BadRequestException.class)))
            }),
            @ApiResponse(responseCode = "404", description = "Папка не найдена", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = NotFoundException.class)))
            })
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> deleteFolder(
            @PathVariable Long id
    ) throws NotFoundException, BadRequestException {
        LOGGER.info("Запрос на удаление папки id=" + id);
        return ResponseEntity.ok(folderService.removeFolder(id));
    }

    @Operation(summary = "Получение списка папок")
    @ApiResponse(responseCode = "200", description = "Список папок получен")
    @GetMapping()
    public ResponseEntity<List<FolderDto>> getFolderList() {
        LOGGER.info("Запрос на получения списка папок");
        return ResponseEntity.ok(folderService.getFolderList());
    }

}

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
import org.example.requestservice.dto.AppResponse;
import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.RequestDto;
import org.example.requestservice.dto.RequestRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.services.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "REQUEST Controller", description = "Управление запросами")
@Controller
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    private static final Logger LOGGER = Logger.getLogger(RequestController.class);

    private final RequestService requestService;

    @Operation(summary = "Добавление запроса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос добавлен успешно"),
            @ApiResponse(responseCode = "400", description = "Не верный запрос", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = BadRequestException.class)))
            })
    })
    @PostMapping()
    public ResponseEntity<RequestDto> addRequest(
            @RequestBody RequestRequest request
    ) throws BadRequestException {
        LOGGER.info("Запрос на добавление запроса");
        return ResponseEntity.ok(requestService.addRequest(request));
    }

    @Operation(summary = "Получение запроса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос получен успешно"),
            @ApiResponse(responseCode = "404", description = "Запрос не найден", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = NotFoundException.class)))
            })
    })
    @GetMapping("/{id}")
    public ResponseEntity<RequestDto> getRequest(
            @PathVariable Long id
    ) throws NotFoundException {
        LOGGER.info("Запрос на получения запроса id=" + id);
        return ResponseEntity.ok(requestService.getRequest(id));
    }

    @Operation(summary = "Удаление запроса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос удален успешно"),
            @ApiResponse(responseCode = "404", description = "Запрос не найден", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = NotFoundException.class)))
            })
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> deleteRequest(
            @PathVariable Long id
    ) throws NotFoundException {
        LOGGER.info("Запрос на удаление запроса id=" + id);
        return ResponseEntity.ok(requestService.removeRequest(id));
    }

    @Operation(summary = "Получение списка запросов",
            description = "Если запросов нет возвращается пустой список")
    @ApiResponse(responseCode = "200", description = "Список получен успешно")
    @GetMapping()
    public ResponseEntity<List<RequestDto>> getRequestList() {
        return ResponseEntity.ok(requestService.getRequestList());
    }

    @Operation(summary = "Добавление тега к запросу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тег успешно добавлен к запросу"),
            @ApiResponse(responseCode = "400", description = "Тег нельзя прикрепить или он уже прикреплен", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = BadRequestException.class)))
            }),
            @ApiResponse(responseCode = "404", description = "Тег или запрос не найдены", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = NotFoundException.class)))
            })
    })
    @PostMapping("/{requestId}/tag/{tagId}")
    public ResponseEntity<AppResponse> addTagToRequest(
            @PathVariable Long requestId,
            @PathVariable Long tagId
    ) throws NotFoundException, BadRequestException {
        LOGGER.info(String.format("Запрос на добавление тега id=%d к запросу id=%d", tagId, requestId));
        return ResponseEntity.ok(requestService.addTagToRequest(tagId, requestId));
    }

    @Operation(summary = "Удаление тега из запроса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тег успешно удален из запроса"),
            @ApiResponse(responseCode = "400", description = "Тег не прикреплен к запросу", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = BadRequestException.class)))
            }),
            @ApiResponse(responseCode = "404", description = "Тег или запрос не найдены", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = NotFoundException.class)))
            })
    })
    @DeleteMapping("/{requestId}/tag/{tagId}")
    public ResponseEntity<AppResponse> removeTagFromRequest(
            @PathVariable Long requestId,
            @PathVariable Long tagId) throws NotFoundException, BadRequestException {
        LOGGER.info(String.format("Запрос на удаление тега id=%d из запроса id=%d", tagId, requestId));
        return ResponseEntity.ok(requestService.removeTagFromRequest(tagId, requestId));
    }

    @Operation(summary = "Получение списка запросов тегу",
            description = "Если запросов нет возвращается пустой список")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список получен удачно"),
            @ApiResponse(responseCode = "404", description = "Тег не найден", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = NotFoundException.class)))
            })
    })
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<RequestDto>> getRequestByTag(
            @PathVariable Long tagId
    ) throws NotFoundException {
        LOGGER.info(String.format("Запрос на получение списка запросов с тегом id=%d", tagId));
        return ResponseEntity.ok(requestService.getRequestListByTag(tagId));
    }

    @Operation(summary = "Добавление запроса в папку",
            description = "Если указана папка, отличная от той, в которой запрос находится сейчас, " +
                    "он будет перенесен в указанную папку ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос успешно добавлен в папку"),
            @ApiResponse(responseCode = "400", description = "Запрос ухе находится в этой папке", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = BadRequestException.class)))
            }),
            @ApiResponse(responseCode = "404", description = "Папка или запрос не найдены", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = NotFoundException.class)))
            })
    })
    @PostMapping("/{requestId}/folder/{folderId}")
    public ResponseEntity<AppResponse> addResponseToFolder(
            @PathVariable Long requestId,
            @PathVariable Long folderId
    ) throws NotFoundException, BadRequestException {
        LOGGER.info(String.format("Запрос на добавление запроса id=%d в папку id=%d", requestId, folderId));
        return ResponseEntity.ok(requestService.addRequestToFolder(requestId, folderId));
    }

    @Operation(summary = "Удаление запроса из папки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос успешно удален из папку"),
            @ApiResponse(responseCode = "400", description = "Запрос не находится в этой папке", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = BadRequestException.class)))
            }),
            @ApiResponse(responseCode = "404", description = "Папка или запрос не найдены", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = NotFoundException.class)))
            })
    })
    @DeleteMapping("/request/{requestId}/folder/{folderId}")
    public ResponseEntity<AppResponse> removeResponseFromFolder(
            @PathVariable Long requestId,
            @PathVariable Long folderId
    ) throws NotFoundException, BadRequestException {
        LOGGER.info(String.format("Запрос на удаление запроса id=%d из папки id=%d", requestId, folderId));
        return ResponseEntity.ok(requestService.removeRequestFromFolder(requestId, folderId));
    }

    @Operation(summary = "Получение списка запросов в папке",
            description = "Если запросов нет возвращается пустой список")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список получен удачно"),
            @ApiResponse(responseCode = "404", description = "Папка не найдена", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = NotFoundException.class)))
            })
    })
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<RequestDto>> getRequestListByFolder(
            @PathVariable Long folderId
    ) throws NotFoundException {
        LOGGER.info(String.format("Запрос на получение списка запросов в папке id=%d", folderId));
        return ResponseEntity.ok(requestService.getRequestListByFolder(folderId));
    }

}

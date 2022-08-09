package org.example.requestservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "Базовый ответ сервиса")
@Getter
public class AppResponse {

    @Schema(description = "Текстовое сообщение", example = "Операция выполнена успешно")
    private final String message;

    @Schema(description = "Время ответа", example = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Moscow")
    private final LocalDateTime time;

    public AppResponse(String message) {
        this.message = message;
        time = LocalDateTime.now();
    }

}

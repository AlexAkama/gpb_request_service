package org.example.requestservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Запрос для создания запроса")
@Getter
public class RequestRequest {

    @Schema(description = "Текст запроса",
            example = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.")
    private String text;

}

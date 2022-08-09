package org.example.requestservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Запрос для создания папки")
@Getter
public class FolderRequest {

    @Schema(description = "Имя папки", example = "Интересно")
    @JsonProperty("folder_name")
    private String name;

}

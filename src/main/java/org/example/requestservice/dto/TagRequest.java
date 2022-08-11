package org.example.requestservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Запрос для создания тега")
@Getter
@Setter
public class TagRequest {

    @Schema(description = "Имя тега", example = "Важно")
    @JsonProperty("tag_name")
    private String name;

}

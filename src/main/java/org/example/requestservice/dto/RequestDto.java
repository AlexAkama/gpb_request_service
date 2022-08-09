package org.example.requestservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.requestservice.model.Request;

import java.time.ZoneId;

@Schema(description = "Объект данных запроса")
@Getter
@NoArgsConstructor
public class RequestDto {

    @Schema(description = "Идентификатор", example = "1")
    private long id;

    @Schema(description = "Текст запроса",
            example = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.")
    private String text;

    @Schema(description = "Дата изменения", example = "1659974644")
    @JsonProperty("modified_date")
    private long modifiedDate;

    @Schema(description = "Длинна текста", example = "74")
    private int length;

    public RequestDto(Request request) {
        id = request.getId();
        text = request.getText();
        modifiedDate = request.getChangeDate().atZone(ZoneId.systemDefault()).toEpochSecond();
        length = request.getLength();
    }

}

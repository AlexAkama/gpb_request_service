package org.example.requestservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.requestservice.model.Folder;

@Schema(description = "Объект данных папки")
@Getter
@NoArgsConstructor
public class FolderDto {

    @Schema(description = "Идентификатор", example = "1")
    private long id;

    @Schema(description = "Имя папки", example = "Интересно")
    @JsonProperty("folder_name")
    private String name;

    public FolderDto(Folder folder) {
        id = folder.getId();
        name = folder.getName();
    }

}

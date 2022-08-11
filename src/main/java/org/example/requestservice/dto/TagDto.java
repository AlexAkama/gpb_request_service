package org.example.requestservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.requestservice.model.Tag;

import java.util.Objects;

@Schema(description = "Объект данных тега")
@Getter
@NoArgsConstructor
public class TagDto {

    @Schema(description = "Идентификатор", example = "1")
    private long id;

    @Schema(description = "Имя тега", example = "Важно")
    @JsonProperty("tag_name")
    private String name;

    public TagDto(Tag tag) {
        id = tag.getId();
        name = tag.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagDto tagDto = (TagDto) o;
        return id == tagDto.id && name.equals(tagDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

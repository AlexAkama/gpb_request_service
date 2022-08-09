package org.example.requestservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ сервиса при удалении объекта")
public class DeleteResponse extends AppResponse{

    public DeleteResponse(String name, Long id) {
        super(String.format("%s id=%d удален успешно", name, id));
    }

}

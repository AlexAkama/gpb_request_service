package org.example.requestservice.exceptions;


public class NotFoundException extends AbstractException {

    public NotFoundException(String objectName, Long id) {
        super(String.format("%s id=%d не найден(а)", objectName, id));
    }

}

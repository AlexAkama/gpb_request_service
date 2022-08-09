package org.example.requestservice.exceptions;

public class BadRequestException extends AbstractException {

    public BadRequestException(String description) {
        super(description);
    }

}

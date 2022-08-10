package org.example.requestservice.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractException extends RuntimeException {

    private final String description;

}

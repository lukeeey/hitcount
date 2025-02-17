package dev.hitcount.api.exceptions;

import lombok.Getter;

@Getter
public class GenericServerErrorException extends RuntimeException {
    private final int statusCode;

    public GenericServerErrorException() {
        this("An unknown error has occurred");
    }

    public GenericServerErrorException(String message) {
        this(500, message);
    }

    public GenericServerErrorException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}

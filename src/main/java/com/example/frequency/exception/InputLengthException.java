package com.example.frequency.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InputLengthException extends RuntimeException {
    public InputLengthException() {
        super();
    }
}

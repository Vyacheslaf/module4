package com.epam.esm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidIdException extends RuntimeException {
    private final long id;
    private final String resourceName;
}

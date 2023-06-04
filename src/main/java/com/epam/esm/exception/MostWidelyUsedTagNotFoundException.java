package com.epam.esm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MostWidelyUsedTagNotFoundException extends RuntimeException {
    private final long userId;
}

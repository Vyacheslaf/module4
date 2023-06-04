package com.epam.esm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidUserOrderIdException extends RuntimeException {
    private final long orderId;
    private final long userId;
}

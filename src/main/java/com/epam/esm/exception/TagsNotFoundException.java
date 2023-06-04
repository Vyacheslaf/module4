package com.epam.esm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagsNotFoundException extends RuntimeException {
    private final long giftCertificateId;
}

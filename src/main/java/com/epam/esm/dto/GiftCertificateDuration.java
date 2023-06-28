package com.epam.esm.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GiftCertificateDuration implements Serializable {
    @NotNull(message = "duration must not be null")
    private Integer duration;
}

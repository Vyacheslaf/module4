package com.epam.esm.dto;

import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class GiftCertificateDto implements Serializable {
    private long id;
    @NotBlank(message = "name of gift certificate must not be empty")
    private String name;

    @NotBlank(message = "description must not be empty")
    private String description;

    @NotNull(message = "price must not be null")
    private Integer price;

    @NotNull(message = "duration must not be null")
    private Integer duration;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
    private Set<Tag> tags;
    private List<Order> orders;

    public GiftCertificateDto(long id, Integer duration) {
        this.id = id;
        this.duration = duration;
    }

    public GiftCertificate convertToGiftCertificate() {
        return new GiftCertificate(0, name, description, price, duration, createDate, lastUpdateDate, tags, orders);
    }
}

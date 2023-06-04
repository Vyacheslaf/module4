package com.epam.esm.dto;

import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class GiftCertificateDto implements Serializable {
    private long id;
    private String name;
    private String description;
    private Integer price;
    private Integer duration;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
    private Set<Tag> tags;
    private List<Order> orders;

    public GiftCertificateDto(long id, Integer duration) {
        this.id = id;
        this.duration = duration;
    }
}

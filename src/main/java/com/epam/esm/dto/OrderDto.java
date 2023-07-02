package com.epam.esm.dto;

import javax.validation.constraints.NotNull;

import com.epam.esm.model.Order;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class OrderDto implements Serializable {
    @NotNull(message = "giftCertificateId must not be null")
    private Long giftCertificateId;

    public Order convertToOrder() {
        Order order = new Order();
        order.setGiftCertificateId(giftCertificateId);
        return order;
    }
}

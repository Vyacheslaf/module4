package com.epam.esm.controller;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.service.GiftCertificateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GiftCertificateControllerTest {

    @InjectMocks
    GiftCertificateController giftCertificateController;

    @Mock
    GiftCertificateService giftCertificateService;

    @Test
    void createTest() {
        long giftCertificateId = 1;
        GiftCertificateDto giftCertificateDto = new GiftCertificateDto();
        giftCertificateDto.setName("name");
        giftCertificateDto.setDescription("description");
        giftCertificateDto.setPrice(111);
        giftCertificateDto.setDuration(222);

        GiftCertificate giftCertificate = giftCertificateDto.convertToGiftCertificate();
        giftCertificate.setId(giftCertificateId);

        when(giftCertificateService.create(any(GiftCertificate.class))).thenReturn(giftCertificate);

        assertEquals(giftCertificateId, giftCertificateController.create(giftCertificateDto).getId());
        assertEquals(giftCertificateDto.getName(), giftCertificateController.create(giftCertificateDto).getName());
        assertEquals(giftCertificateDto.getDescription(),
                     giftCertificateController.create(giftCertificateDto).getDescription());
        assertEquals(giftCertificateDto.getPrice(), giftCertificateController.create(giftCertificateDto).getPrice());
        assertEquals(giftCertificateDto.getDuration(),
                     giftCertificateController.create(giftCertificateDto).getDuration());
    }
}

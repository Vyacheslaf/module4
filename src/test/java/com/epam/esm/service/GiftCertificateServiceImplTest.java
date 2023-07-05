package com.epam.esm.service;

import com.epam.esm.model.GiftCertificate;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.mapper.GiftCertificateMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GiftCertificateServiceImplTest {

    @InjectMocks
    GiftCertificateServiceImpl giftCertificateService;

    @Mock
    GiftCertificateRepository giftCertificateRepository;

    @Mock
    TagRepository tagRepository;

    @Mock
    GiftCertificateMapper giftCertificateMapper;

    @Test
    void createTest() {
        GiftCertificate giftCertificate = new GiftCertificate();

        when(giftCertificateRepository.save(any(GiftCertificate.class))).thenReturn(giftCertificate);

        GiftCertificate actualGiftCertificate = giftCertificateService.create(giftCertificate);
        assertNotNull(actualGiftCertificate.getCreateDate());
        assertNotNull(actualGiftCertificate.getLastUpdateDate());
    }
}

package com.epam.esm.service;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import com.epam.esm.dto.SearchCriteria;

import java.util.List;

public interface GiftCertificateService {
    GiftCertificate findById(long id);
    List<GiftCertificate> findAll(SearchCriteria sc);
    GiftCertificate create(GiftCertificate certificate);
    GiftCertificate update(GiftCertificateDto dto);
    void delete(long id);
    List<Tag> findGiftCertificateTags(long id, int page, int size);
}

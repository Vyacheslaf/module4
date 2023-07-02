package com.epam.esm.service;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.exception.*;
import com.epam.esm.model.Tag;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.specification.GiftCertificateSpecification;
import com.epam.esm.service.mapper.GiftCertificateMapper;
import com.epam.esm.dto.SearchCriteria;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {
    private static final String TIMEZONE = "UTC";
    private static final String SORT_PATTERN = "^((name|createDate|lastUpdateDate).(asc|desc))$";
    private static final String SORT_DIRECTION_ASC = "asc";
    private static final String SORT_DIRECTION_DESC = "desc";
    private static final String SORT_REPLACE_PATTERN = "\\.";

    private final GiftCertificateRepository giftCertificateRepository;
    private final TagRepository tagRepository;
    private final GiftCertificateMapper mapper;

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateRepository giftCertificateRepository,
                                      TagRepository tagRepository, GiftCertificateMapper mapper) {
        this.giftCertificateRepository = giftCertificateRepository;
        this.tagRepository = tagRepository;
        this.mapper = mapper;
    }

    @Override
    public GiftCertificate findById(long id) {
        return giftCertificateRepository.findById(id)
                .orElseThrow(() -> new InvalidIdException(id, GiftCertificate.class.getSimpleName()));
    }

    @Override
    public List<GiftCertificate> findAll(SearchCriteria sc) {
        Pageable pageable = getPageable(sc);
        GiftCertificateSpecification giftCertificateSpecification = new GiftCertificateSpecification(sc);
        return giftCertificateRepository.findAll(giftCertificateSpecification, pageable).getContent();
    }

    private Pageable getPageable(SearchCriteria sc) {
        if ((sc.getSortList() == null) || sc.getSortList().isEmpty()) {
            return PageRequest.of(sc.getPage(), sc.getSize());
        }
        List<Sort.Order> orders = new ArrayList<>();
        Map<String, Sort.Direction> directionMap = new HashMap<>();
        directionMap.put(SORT_DIRECTION_ASC, Sort.Direction.ASC);
        directionMap.put(SORT_DIRECTION_DESC, Sort.Direction.DESC);
        for (String sort : sc.getSortList()) {
            if ((sort == null) || !sort.matches(SORT_PATTERN)) {
                throw new InvalidSortRequestException(SORT_PATTERN.substring(2, SORT_PATTERN.length() - 5));
            }
            String[] splitSort = sort.split(SORT_REPLACE_PATTERN);
            orders.add(new Sort.Order(directionMap.get(splitSort[1]), splitSort[0]));
        }
        return PageRequest.of(sc.getPage(), sc.getSize(), Sort.by(orders));
    }

    @Override
    @Transactional
    public GiftCertificate create(GiftCertificate certificate) {
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of(TIMEZONE));
        if (certificate.getCreateDate() == null) {
            certificate.setCreateDate(currentDateTime);
        }
        if (certificate.getLastUpdateDate() == null) {
            certificate.setLastUpdateDate(currentDateTime);
        }
        Set<Tag> tags = prepareTags(certificate.getTags());
        certificate.setTags(tags);
        return giftCertificateRepository.save(certificate);
    }

    @Override
    @Transactional
    public GiftCertificate update(GiftCertificateDto dto) {
        if (dto.getLastUpdateDate() == null) {
            dto.setLastUpdateDate(LocalDateTime.now(ZoneId.of(TIMEZONE)));
        }
        Set<Tag> tags = prepareTags(dto.getTags());
        dto.setTags(tags);
        GiftCertificate giftCertificate = giftCertificateRepository.findById(dto.getId())
                .orElseThrow(() -> new InvalidIdException(dto.getId(), GiftCertificate.class.getSimpleName()));
        mapper.updateGiftCertificateFromDto(dto, giftCertificate);
        return giftCertificateRepository.save(giftCertificate);
    }

    private Set<Tag> prepareTags(Set<Tag> tags) {
        if (tags == null) {
            return tags;
        }
        if (!tags.isEmpty()
                && (tags.contains(null) || (tags.stream().map(Tag::getName).anyMatch(StringUtils::isBlank)))) {
            throw new InvalidTagNameException();
        }
        return tags.stream().map(t -> tagRepository.findByName(t.getName()).orElse(t))
                .collect(Collectors.toSet());
    }
    @Override
    @Transactional
    public void delete(long id) {
        if (!giftCertificateRepository.existsById(id)) {
            throw new InvalidIdException(id, GiftCertificate.class.getSimpleName());
        }
        giftCertificateRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<Tag> findGiftCertificateTags(long giftCertificateId, int page, int size) {
        if (!giftCertificateRepository.existsById(giftCertificateId)) {
            throw new InvalidIdException(giftCertificateId, GiftCertificate.class.getSimpleName());
        }
        List<Tag> tags = tagRepository.findTagsByGiftCertificatesId(giftCertificateId, PageRequest.of(page, size));
        if ((tags == null) || tags.isEmpty()) {
            throw new TagsNotFoundException(giftCertificateId);
        }
        return tags;
    }
}

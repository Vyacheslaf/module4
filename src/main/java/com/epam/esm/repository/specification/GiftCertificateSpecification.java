package com.epam.esm.repository.specification;

import com.epam.esm.exception.InvalidTagNameException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftCertificate_;
import com.epam.esm.model.Tag;
import com.epam.esm.model.Tag_;
import com.epam.esm.dto.SearchCriteria;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GiftCertificateSpecification implements Specification<GiftCertificate> {
    private static final String SQL_ANY_SYMBOL = "%";
    private final SearchCriteria sc;

    public GiftCertificateSpecification(SearchCriteria sc) {
        this.sc = sc;
    }

    @Override
    public Predicate toPredicate(Root<GiftCertificate> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if ((sc.getTagNameList() != null) && !sc.getTagNameList().isEmpty()) {
            for (String tagName : sc.getTagNameList()) {
                if (StringUtils.isBlank(tagName)) {
                    throw new InvalidTagNameException();
                }

                Subquery<Long> subquery = cq.subquery(Long.class);
                Root<GiftCertificate> subqueryRoot = subquery.from(GiftCertificate.class);
                Join<GiftCertificate, Tag> tagGiftCertificateJoin = subqueryRoot.join(GiftCertificate_.TAGS);

                subquery.select(subqueryRoot.get(GiftCertificate_.ID))
                        .where(cb.equal(tagGiftCertificateJoin.get(Tag_.NAME), tagName));

                predicates.add(cb.in(root.get(GiftCertificate_.ID)).value(subquery));
            }
        }

        String searchPattern = SQL_ANY_SYMBOL + sc.getSearch() + SQL_ANY_SYMBOL;
        if ((sc.getSearch() != null) && !sc.getSearch().isEmpty()) {
            predicates.add(cb.or(
                    cb.like(root.get(GiftCertificate_.NAME), searchPattern),
                    cb.like(root.get(GiftCertificate_.DESCRIPTION), searchPattern)));
        }

        if (!predicates.isEmpty()) {
            return cb.and(predicates.toArray(new Predicate[0]));
        }
        return null;
    }
}

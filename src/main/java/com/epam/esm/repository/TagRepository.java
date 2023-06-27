package com.epam.esm.repository;

import com.epam.esm.model.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
//    @Query("SELECT t FROM GiftCertificate gc JOIN gc.tags t WHERE gc.id = :giftCertificateId")
//    List<Tag> findTagsByGiftCertificatesId(@Param("giftCertificateId") long giftCertificateId, Pageable pageable);
    List<Tag> findTagsByGiftCertificatesId(long giftCertificateId, Pageable pageable);

    @Query("SELECT t FROM Order o JOIN o.giftCertificate gc JOIN gc.tags t WHERE o.userId = :userId " +
            "GROUP BY t.id ORDER BY SUM(o.cost) DESC")
    List<Tag> findMostWidelyUsedTagsByUserId(@Param("userId") long userId, Pageable pageable);
}

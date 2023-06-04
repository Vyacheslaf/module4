package com.epam.esm.repository;

import com.epam.esm.model.GiftCertificate;
import com.epam.esm.repository.projection.GiftCertificateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GiftCertificateRepository extends JpaRepository<GiftCertificate, Long>,
                                                    JpaSpecificationExecutor<GiftCertificate> {
    Optional<GiftCertificateProjection> findPriceById(long id);
}

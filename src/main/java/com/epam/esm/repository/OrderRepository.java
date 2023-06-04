package com.epam.esm.repository;

import com.epam.esm.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    Optional<Order> findByIdAndUserId(long id, long userId);
    List<Order> findAllByUserId(long userId, Pageable pageable);
}

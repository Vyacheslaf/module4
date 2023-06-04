package com.epam.esm.service;

import com.epam.esm.exception.MostWidelyUsedTagNotFoundException;
import com.epam.esm.exception.InvalidIdException;
import com.epam.esm.exception.InvalidUserOrderIdException;
import com.epam.esm.model.*;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private static final String TIMEZONE = "UTC";

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final GiftCertificateRepository giftCertificateRepository;
    private final TagRepository tagRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           OrderRepository orderRepository,
                           GiftCertificateRepository giftCertificateRepository,
                           TagRepository tagRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.giftCertificateRepository = giftCertificateRepository;
        this.tagRepository = tagRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size)).getContent();
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new InvalidIdException(id, User.class.getSimpleName()));
    }

    @Override
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    @Override
    public List<Order> findOrdersByUserId(long id, int page, int size) {
        return orderRepository.findAllByUserId(id, PageRequest.of(page, size));
    }

    @Override
    public Order findOrderByIdAndUserId(long orderId, long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new InvalidUserOrderIdException(orderId, userId));
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        if (!userRepository.existsById(order.getUserId())) {
            throw new InvalidIdException(order.getUserId(), User.class.getSimpleName());
        }
        int cost = giftCertificateRepository.findPriceById(order.getGiftCertificateId())
                .orElseThrow(() -> new InvalidIdException(order.getGiftCertificateId(),
                                                          GiftCertificate.class.getSimpleName()))
                .getPrice();
        order.setCost(cost);
        order.setPurchaseDate(LocalDateTime.now(ZoneId.of(TIMEZONE)));
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Tag findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new InvalidIdException(userId, User.class.getSimpleName());
        }
        List<Tag> tags = tagRepository.findMostWidelyUsedTagsByUserId(userId, PageRequest.of(0, 1));
        if (tags.isEmpty()) {
            throw new MostWidelyUsedTagNotFoundException(userId);
        }
        return tags.get(0);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}

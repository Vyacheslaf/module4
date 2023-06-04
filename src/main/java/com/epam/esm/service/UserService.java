package com.epam.esm.service;

import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll(int page, int size);
    User findById(long id);
    User create(User user);
    List<Order> findOrdersByUserId(long id, int page, int size);
    Order findOrderByIdAndUserId(long orderId, long userId);
    Order createOrder(Order order);
    Tag findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(long userId);
}

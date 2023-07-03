package com.epam.esm.controller;

import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.model.Order;
import com.epam.esm.model.User;
import com.epam.esm.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    @Test
    void createUserTest() {
        UserDto userDto = new UserDto();
        userDto.setUsername("username");
        userDto.setEmail("email");
        userDto.setPassword("password");

        User user = userDto.convertToUser();
        user.setId(1);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        when(userService.create(any(User.class))).thenReturn(user);

        assertEquals(userDto.getUsername(), userController.createUser(userDto).getUsername());
        assertEquals(userDto.getEmail(), userController.createUser(userDto).getEmail());
        assertTrue(passwordEncoder.matches(userDto.getPassword(), userController.createUser(userDto).getPassword()));
    }

    @Test
    void createOrderTest() {
        long giftCertificateId = 1;
        long userId = 1;

        OrderDto orderDto = new OrderDto();
        orderDto.setGiftCertificateId(giftCertificateId);

        Order order = new Order();
        order.setUserId(userId);
        order.setGiftCertificateId(giftCertificateId);
        when(userService.createOrder(any(Order.class))).thenReturn(order);

        assertEquals(orderDto.getGiftCertificateId(),
                     userController.createOrder(orderDto, userId).getGiftCertificateId());
    }

    @Test
    void findByIdTest() {
        User user = new User();
        user.setId(1);

        when(userService.findById(1)).thenReturn(user);

        assertEquals(1, userController.findById(1).getId());
    }
}

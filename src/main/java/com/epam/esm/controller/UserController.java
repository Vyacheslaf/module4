package com.epam.esm.controller;

import com.epam.esm.model.Order;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.exception.NoContentException;
import com.epam.esm.service.UserService;
import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonView;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/jpa/user")
@Validated
public class UserController {
    private static final String PARENT_RELATION_NAME = "parent";
    private static final String CERTIFICATE_RELATION_NAME = "giftCertificate";
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(null);
    }

    @JsonView(Views.ShortView.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(produces = "application/hal+json")
    public CollectionModel<User> findAll(@RequestParam(defaultValue = "0") Integer page,
                                         @RequestParam(defaultValue = "5") Integer size) {
        List<User> users = userService.findAll(page, size);
        if (users.isEmpty()) {
            throw new NoContentException();
        }
        for (User user : users) {
            Link link = linkTo(methodOn(UserController.class).findById(user.getId())).withSelfRel();
            user.add(link);
        }
        Link link = linkTo(methodOn(UserController.class).findAll(page, size)).withSelfRel();
        return CollectionModel.of(users, link);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("(hasAuthority('USER') and #id == authentication.principal) or hasAuthority('ADMIN')")
    @GetMapping(value = "/{id}", produces = "application/hal+json")
    public User findById(@PathVariable long id) {
        User user = userService.findById(id);
        user.add(linkTo(UserController.class).slash(id).withSelfRel());
        user.add(linkTo(UserController.class).withRel(LinkRelation.of(PARENT_RELATION_NAME)));
        user.add(linkTo(methodOn(UserController.class).findUserOrders(id, 0, 5))
                .withRel(IanaLinkRelations.COLLECTION));
        return user;
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/hal+json")
    public User createUser(@Valid @RequestBody User user) {
        user = userService.create(user);
        user.add(linkTo(UserController.class).slash(user.getId()).withSelfRel());
        user.add(linkTo(UserController.class).withRel(LinkRelation.of(PARENT_RELATION_NAME)));
        user.add(linkTo(methodOn(UserController.class).findUserOrders(user.getId(), 0, 5))
                .withRel(IanaLinkRelations.COLLECTION));
        return user;
    }

    @JsonView(Views.ShortView.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("(hasAuthority('USER') and #id == authentication.principal) or hasAuthority('ADMIN')")
    @GetMapping(value = "/{id}/order", produces = "application/hal+json")
    public CollectionModel<Order> findUserOrders(@PathVariable long id,
                                                 @RequestParam(defaultValue = "0") Integer page,
                                                 @RequestParam(defaultValue = "5") Integer size) {
        List<Order> orders = userService.findOrdersByUserId(id, page, size);
        if (orders.isEmpty()) {
            throw new NoContentException();
        }
        for (Order order : orders) {
            Link link = linkTo(methodOn(UserController.class).findUserOrderByIds(id, order.getId())).withSelfRel();
            order.add(link);
        }
        Link selflink = linkTo(methodOn(UserController.class).findUserOrders(id, page, size)).withSelfRel();
        Link userLink = linkTo(methodOn(UserController.class).findById(id))
                                .withRel(LinkRelation.of(PARENT_RELATION_NAME));
        return CollectionModel.of(orders, selflink, userLink);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("(hasAuthority('USER') and #userId == authentication.principal) or hasAuthority('ADMIN')")
    @GetMapping(value = "/{userId}/order/{orderId}", produces = "application/hal+json")
    public Order findUserOrderByIds(@PathVariable long userId,
                                    @PathVariable long orderId) {
        Order order = userService.findOrderByIdAndUserId(orderId, userId);
        return addLinksToOrder(userId, order);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("(hasAuthority('USER') and #id == authentication.principal) or hasAuthority('ADMIN')")
    @PostMapping(value = "/{id}/order",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/hal+json")
    public Order createOrder(@Valid @RequestBody Order order,
                             @PathVariable long id) {
        order.setUserId(id);
        order = userService.createOrder(order);
        return addLinksToOrder(id, order);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/{id}/tag", produces = "application/hal+json")
    public Tag findMostWidelyUsedTagWithHighestCostOfAllOrders(@PathVariable long id) {
        Tag tag = userService.findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(id);
        tag.add(linkTo(methodOn(UserController.class)
                .findMostWidelyUsedTagWithHighestCostOfAllOrders(id)).withSelfRel());
        tag.add(linkTo(methodOn(UserController.class).findById(id)).withRel(LinkRelation.of(PARENT_RELATION_NAME)));
        return tag;
    }

    private Order addLinksToOrder(long userId, Order order) {
        order.add(linkTo(methodOn(UserController.class).findUserOrderByIds(userId, order.getId())).withSelfRel());
        order.add(linkTo(methodOn(UserController.class).findUserOrders(userId, 0, 5))
                .withRel(LinkRelation.of(PARENT_RELATION_NAME)));
        order.add(linkTo(methodOn(GiftCertificateController.class).findById(order.getGiftCertificateId()))
                .withRel(LinkRelation.of(CERTIFICATE_RELATION_NAME)));
        return order;
    }
}

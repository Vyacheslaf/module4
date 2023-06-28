package com.epam.esm.controller;

import com.epam.esm.dto.JwtRequest;
import com.epam.esm.dto.JwtResponse;
import com.epam.esm.service.AuthenticationService;
import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonView;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jpa/login")
@Validated
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(null);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(produces = { "application/json" })
    public JwtResponse login(@Valid @RequestBody JwtRequest jwtRequest) {
        return authenticationService.login(jwtRequest);
    }
}

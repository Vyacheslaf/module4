package com.epam.esm.service;

import com.epam.esm.dto.JwtRequest;
import com.epam.esm.dto.JwtResponse;

public interface AuthenticationService {
    JwtResponse login(JwtRequest jwtRequest);
}

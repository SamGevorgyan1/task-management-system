package com.taskmanagement.controller;


import com.taskmanagement.dto.requestdto.AuthenticationDTO;
import com.taskmanagement.dto.requestdto.UserDTO;
import com.taskmanagement.dto.responsedto.AuthenticationResponseDTO;
import com.taskmanagement.exceptions.AuthApiException;
import com.taskmanagement.exceptions.AuthBadRequestException;
import com.taskmanagement.exceptions.UserApiException;
import com.taskmanagement.exceptions.UserBadRequestException;
import com.taskmanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthenticationResponseDTO login(@RequestBody AuthenticationDTO authenticationDTO) throws AuthBadRequestException, AuthApiException, UserBadRequestException {
        return authService.login(authenticationDTO);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public boolean register(@RequestBody @Valid UserDTO userDTO) throws AuthApiException, UserBadRequestException {
        return authService.register(userDTO);
    }
}
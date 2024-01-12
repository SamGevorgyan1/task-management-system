package com.taskmanagement.service.impl;

import com.taskmanagement.dto.requestdto.UserDTO;
import com.taskmanagement.enums.UserStatus;
import com.taskmanagement.exceptions.*;
import com.taskmanagement.model.UserEntity;
import com.taskmanagement.dto.requestdto.AuthenticationDTO;
import com.taskmanagement.dto.responsedto.AuthenticationResponseDTO;
import com.taskmanagement.security.jwt.JwtService;
import com.taskmanagement.service.AuthService;
import com.taskmanagement.service.TokenService;
import com.taskmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.taskmanagement.validator.UserValidator.passwordValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JwtService jwtService;

    private final TokenService tokenService;

    @Override
    public AuthenticationResponseDTO login(AuthenticationDTO authenticationDTO) throws AuthApiException, AuthBadRequestException {

        UserEntity user;

        try {
            passwordValidator(authenticationDTO.getPassword());
        } catch (UserBadRequestException e) {
            throw new AuthBadRequestException("Password must contain at list one digit and 2 uppercase");
        }

        try {
            user = userService.findByEmail(authenticationDTO.getEmail());
        } catch (UserApiException e) {
            throw new AuthApiException("Error during authentication");
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("Wrong email or password");
        } catch (UserBadRequestException e) {
            throw new RuntimeException();
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new AuthBadRequestException("User account is inactive. Verify your account before logging in.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationDTO.getEmail(), authenticationDTO.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            Map<String, Object> claim = buildTokenClaims(user);

            String jwtToken = jwtService.generateToken(claim, userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            tokenService.revokeAllUserTokens(user);
            tokenService.saveUserToken(user, jwtToken);

            return AuthenticationResponseDTO.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (BadCredentialsException e) {
            throw new AuthBadRequestException("Wrong email or password");
        } catch (TokenApiException e) {
            throw new AuthApiException("Error during authentication");
        }
    }

    @Override
    public boolean register(UserDTO userDTO) throws AuthApiException, UserBadRequestException {

        passwordValidator(userDTO.getPassword());

        try {
            userService.createUser(userDTO);
        } catch (UserApiException e) {
            throw new AuthApiException("Error during creating user");
        }

        return true;
    }

    private Map<String, Object> buildTokenClaims(UserEntity user) {
        Map<String, Object> claim = new HashMap<>();
        claim.put("id", user.getId());
        claim.put("email", user.getEmail());
        claim.put("name", user.getName());
        claim.put("surname", user.getSurname());
        claim.put("year", user.getYear());
        claim.put("role", user.getRole());
        return claim;
    }
}
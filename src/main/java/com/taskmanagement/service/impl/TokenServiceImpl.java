package com.taskmanagement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanagement.dto.responsedto.AuthenticationResponseDTO;
import com.taskmanagement.enums.TokenType;
import com.taskmanagement.exceptions.TokenApiException;
import com.taskmanagement.model.TokenEntity;
import com.taskmanagement.model.UserEntity;
import com.taskmanagement.repository.TokenRepository;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.security.CustomUserDetails;
import com.taskmanagement.security.jwt.JwtService;
import com.taskmanagement.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    @Override
    public void saveUserToken(UserEntity user, String jwtToken) throws TokenApiException {
        TokenEntity token = TokenEntity.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        try {
            tokenRepository.save(token);
        } catch (Exception e) {
            throw new TokenApiException("Error during saving token");
        }
    }


    @Override
    public void revokeAllUserTokens(UserEntity user) throws TokenApiException {
        List<TokenEntity> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        try {
            tokenRepository.saveAll(validUserTokens);
        } catch (Exception e) {
            throw new TokenApiException("Error during saving token");
        }
    }


    @Override
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws TokenApiException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken;
        String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            UserEntity user;
            user = userRepository.findByEmail(userEmail);

            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            if (jwtService.isTokenValid(refreshToken, customUserDetails)) {
                String accessToken = jwtService.generateToken(customUserDetails);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                AuthenticationResponseDTO authResponse = AuthenticationResponseDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
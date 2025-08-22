package com.francis.angularbackend.controllers;

import com.francis.angularbackend.config.JwtConfig;
import com.francis.angularbackend.dtos.JwtResponse;
import com.francis.angularbackend.dtos.LoginRequest;
import com.francis.angularbackend.dtos.UserDto;
import com.francis.angularbackend.mappers.UserMapper;
import com.francis.angularbackend.repositories.UserRepositories;
import com.francis.angularbackend.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final UserRepositories userRepositories;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );

        var user =  userRepositories.findByEmail(request.getEmail()).orElseThrow();

        var accessToken =  jwtService.generateAccessToken(user);
        var refreshToken =  jwtService.generateRefreshToken(user);

        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken));
    }


    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @CookieValue(value = "refreshToken") String refreshToken
    ){
    if(!jwtService.validateToken(refreshToken)){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }

    var userId = jwtService.getUserIdFromToken(refreshToken);
    var user = userRepositories.findById(userId).orElseThrow();
    var accessToken =  jwtService.generateAccessToken(user);

    return ResponseEntity.ok(new JwtResponse(accessToken));

    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        var authentication =  SecurityContextHolder.getContext().getAuthentication();
        var email = (String) authentication.getPrincipal();

        var user = userRepositories.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        var userDto = userMapper.toDto(user);

        return ResponseEntity.ok(userDto);

    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handlerBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}


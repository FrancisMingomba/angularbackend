package com.francis.angularbackend.controllers;

import com.francis.angularbackend.dtos.ChangePasswordRequest;
import com.francis.angularbackend.dtos.RegisterUserRequest;
import com.francis.angularbackend.dtos.UpdateUserRequest;
import com.francis.angularbackend.dtos.UserDto;
import com.francis.angularbackend.mappers.UserMapper;
import com.francis.angularbackend.repositories.UserRepositories;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepositories userRepositories;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("")
    public Iterable<UserDto> getAllUsers() {

        return userRepositories.findAll()
                .stream()
                .map( userMapper::toDto )
                .toList();
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable long id) {
        var user = userRepositories.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        var userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    @Operation(summary = "Add user to database.")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder) {

         if (userRepositories.existsByEmail(request.getEmail())) {
             return ResponseEntity.badRequest().body(
                     Map.of("email", "Email is already registered."));
         }
       var user =  userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
       userRepositories.save(user);

       var userDto = userMapper.toDto(user);
      var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();

       return ResponseEntity.created(uri).body(userDto);

    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable (name = "id") Long id,
            @RequestBody UpdateUserRequest request) {
        var user = userRepositories.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userMapper.updateUser(request, user);
        userRepositories.save(user);

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        var user = userRepositories.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userRepositories.delete(user);
        return ResponseEntity.noContent().build();

    }
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        var user = userRepositories.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (!user.getPassword().equals(request.getOldPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(request.getNewPassword());
        userRepositories.save(user);
        return ResponseEntity.noContent().build();
    }


}

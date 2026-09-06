package com.muneer.store.controllers;

import com.muneer.store.dtos.*;
import com.muneer.store.mappers.ProductMapper;
import com.muneer.store.mappers.UserMapper;
import com.muneer.store.repositories.ProductRepository;
import com.muneer.store.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users")

public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @Operation(summary = "Get all users")
    public Iterable <UserDto> getAllUsers(
            @Parameter(description = "The field used to sort users: name or email")
            @RequestParam(required = false, defaultValue = "", name = "sort") String sort){
        if (!Set.of("name", "email").contains(sort))
            sort = "name";
        return userRepository.findAll(Sort.by(sort))
                .stream().map(userMapper::toDto).toList();
    }
    @PostMapping
    @Operation(summary = "Register a new user")
    public ResponseEntity<?> registerUser
            (@Valid @RequestBody RegisterUserRequest request,
                                              UriComponentsBuilder uriBuilder){
        if (userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(Map.of("email", "Email is already exist"));
        }
        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        var userDto = userMapper.toDto(user);
       var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);

    }
    @PutMapping("/{id}")
    @Operation(summary = "Update a user")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "The ID of the user")
            @PathVariable long id,
            @RequestBody UpdateUserRequest request){
        var user = userRepository.findById(id).orElse(null);

        if (user == null){
            return ResponseEntity.notFound().build();
        }
        else {
            userMapper.update(request, user);

            userRepository.save(user);

            return ResponseEntity.ok(userMapper.toDto(user));
        }
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "The ID of the user")
            @PathVariable long id){
        var user = userRepository.findById(id).orElse(null);

        if (user == null){
            return ResponseEntity.notFound().build();
        }
        else {
            userRepository.delete(user);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user")
    public ResponseEntity <UserDto> getUser(
            @Parameter(description = "The ID of the user")
            @PathVariable long id){

        var user = userRepository.findById(id).orElse(null);
        if (user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }
    @PostMapping("/{id}/change-password")
    @Operation(summary = "Change a user's password")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "The ID of the user")
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request
            ){
        var user = userRepository.findById(id).orElse(null);
        if (user == null){
            return ResponseEntity.noContent().build();
        }

        if (!user.getPassword().equals(request.getOldPassword())){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.noContent().build();

    }
}

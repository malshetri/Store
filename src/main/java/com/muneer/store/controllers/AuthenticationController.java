package com.muneer.store.controllers;

import com.muneer.store.dtos.JwtResponse;
import com.muneer.store.dtos.LoginRequest;
import com.muneer.store.dtos.UserDto;
import com.muneer.store.mappers.UserMapper;
import com.muneer.store.repositories.UserRepository;
import com.muneer.store.services.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/auth")

public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request
            ) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()
        ));
        var token = jwtService.generateToken(request.getEmail());

        return ResponseEntity.ok(new JwtResponse(token));
    }
    @PostMapping("/validate")
    public boolean validate(@RequestHeader ("Authorization") String authHeader){
        System.out.println("Validate called");

        var token = authHeader.replace("Bearer ", ""); // Bearer
        return jwtService.validateToken(token);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(){

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var email = (String) authentication.getPrincipal();

        var user = userRepository.findByEmail(email).orElse(null);

        if (user == null){
            return ResponseEntity.notFound().build();
        }
        var userDto = userMapper.toDto(user);

        return ResponseEntity.ok(userDto);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsExeption(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
}

package com.codeWithProyect.HotelServer.controller.auth;


import com.codeWithProyect.HotelServer.Entity.User;
import com.codeWithProyect.HotelServer.dto.AuthenticationRequest;
import com.codeWithProyect.HotelServer.dto.AuthenticationResponse;
import com.codeWithProyect.HotelServer.dto.SignupRequest;

import com.codeWithProyect.HotelServer.dto.UserDto;
import com.codeWithProyect.HotelServer.repository.UserRepository;
import com.codeWithProyect.HotelServer.services.auth.AuthService;
import com.codeWithProyect.HotelServer.services.jwt.UserService;
import com.codeWithProyect.HotelServer.util.JwtUtil;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final UserService userService;


    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest signupRequest){
        try {
            UserDto createUser = authService.createUser(signupRequest);
            return new ResponseEntity<>(createUser, HttpStatus.OK);
        }catch (EntityExistsException entityExistsException){
            return new ResponseEntity<>("User already exist", HttpStatus.NOT_ACCEPTABLE);
        }catch (Exception e){
            return new ResponseEntity<>("User not created, come again later", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e){
            throw  new BadCredentialsException("Incorrect username or password");
        }

       final UserDetails userDetails = userService.userDetailsService().loadUserByUsername(authenticationRequest.getEmail());
        Optional<User> optionalUser = userRepository.findFisrtByEmail(userDetails.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);


        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        if(optionalUser.isPresent()){
            authenticationResponse.setJwt(jwt);
            authenticationResponse.setUserRole(optionalUser.get().getUserRole());
            authenticationResponse.setUserId(optionalUser.get().getId());
        }
        return authenticationResponse;
    }
}

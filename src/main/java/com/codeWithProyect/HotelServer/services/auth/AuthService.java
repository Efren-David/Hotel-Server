package com.codeWithProyect.HotelServer.services.auth;

import com.codeWithProyect.HotelServer.dto.SignupRequest;

import com.codeWithProyect.HotelServer.dto.UserDto;

public interface AuthService {

    UserDto createUser(SignupRequest singupRequest);
}

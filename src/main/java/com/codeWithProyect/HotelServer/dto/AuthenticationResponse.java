package com.codeWithProyect.HotelServer.dto;

import com.codeWithProyect.HotelServer.enums.UserRole;
import lombok.Data;

@Data
public class AuthenticationResponse {

    private String jwt;

    private Long userId;

    private UserRole userRole;
}

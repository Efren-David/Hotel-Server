package com.codeWithProyect.HotelServer.repository;

import com.codeWithProyect.HotelServer.Entity.User;
import com.codeWithProyect.HotelServer.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findFisrtByEmail(String email);

    Optional<User> findByUserRole(UserRole userRole);

}

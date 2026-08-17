package com.manage.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manage.model.Otp;

public interface OtpRepo extends JpaRepository<Otp, Long> {

    Optional<Otp> findByEmail(String email);

    Optional<Otp> findByEmailAndOtp(String email, String otp);
}
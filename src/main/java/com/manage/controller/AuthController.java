package com.manage.controller;
import java.time.LocalDateTime;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manage.dto.AuthResponse;
import com.manage.dto.ForgotPasswordRequest;
import com.manage.dto.LoginRequest;
import com.manage.dto.ProfileResponseDTO;
import com.manage.dto.RegisterRequest;
import com.manage.dto.ResetPasswordRequest;
import com.manage.dto.VerifyOtpRequest;
import com.manage.model.User;
import com.manage.repo.UserRepo;
import com.manage.service.EmailService;
import com.manage.service.OtpService;
import com.manage.service.UserService;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserService service;
	
	@Autowired
	private EmailService emailService;
	
	

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest dto) {
		return ResponseEntity.ok(service.register(dto));
	}
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
	        @RequestBody LoginRequest dto) {

	    return ResponseEntity.ok(service.login(dto));
	}
	
	@Autowired
	private OtpService otpService;

	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(
	        @RequestBody ForgotPasswordRequest dto) {

	    return ResponseEntity.ok(
	            service.forgotPassword(dto)
	    );
	}
	
	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request) {

	    String message = otpService.verifyOtp(
	            request.getEmail(),
	            request.getOtp());

	    return ResponseEntity.ok(message);
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(
	        @RequestBody ResetPasswordRequest request) {

	    String message = otpService.resetPassword(
	            request.getEmail(),
	            request.getNewPassword());

	    return ResponseEntity.ok(message);
	}
	@GetMapping("/profile")
	public ResponseEntity<ProfileResponseDTO> profile(Authentication authentication) {

	    return ResponseEntity.ok(service.getProfile(authentication.getName()));

	}
	
	@GetMapping("/test-email")
	public String testEmail() {

	    emailService.sendOtp(
	            "borhadeparth24@gmail.com",
	            "123456"
	    );

	    return "Email Sent Successfully";
	}
}

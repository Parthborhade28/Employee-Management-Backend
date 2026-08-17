package com.manage.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import com.manage.model.Otp;
import com.manage.model.User;
import com.manage.repo.OtpRepo;
import com.manage.repo.UserRepo;

@Service
public class OtpService {

    @Autowired
    private OtpRepo  otpRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserRepo userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String generateOtp(String email) {

        // Generate 6-digit OTP
        Random random = new Random();
        String otp = String.valueOf(100000 + random.nextInt(900000));

        // Check if OTP already exists for this email
        Otp otpEntity = otpRepository.findByEmail(email)
                .orElse(new Otp());

        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setVerified(false);

        // OTP valid for 5 minutes
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otpEntity);
        
        emailService.sendOtp(email, otp);

        return otp;
    }
    


    public String verifyOtp(String email, String otp) {

        Otp otpEntity = otpRepository.findByEmailAndOtp(email, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }

        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);

        return "OTP Verified Successfully";
    }
    public String resetPassword(String email, String newPassword) {

        Otp otpEntity = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (!otpEntity.isVerified()) {
            throw new RuntimeException("Please verify OTP first");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        // Invalidate OTP after successful password reset
        otpRepository.delete(otpEntity);

        return "Password Reset Successfully";
    }
}
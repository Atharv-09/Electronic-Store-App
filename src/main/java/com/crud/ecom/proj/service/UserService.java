package com.crud.ecom.proj.service;

import com.crud.ecom.proj.model.Users;
import com.crud.ecom.proj.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.crud.ecom.proj.service.OTPService;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTService jwtService;

    @Autowired
    OTPService otpService;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public Users register(Users user) {

        String generatedOTP = otpService.generateOTPForUser(user);

        if(user.getOtp().equals(generatedOTP)) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }else{
            throw new RuntimeException("Invalid OTP");
        }
        userRepository.save(user);
        return user;
    }
    // it will authenticate the user is thier or not
    public String verify(Users user) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));

        if(authentication.isAuthenticated()){
            // Generate token
            String tok = jwtService.generateToken(user.getUsername());
            System.out.println(tok);
            return tok;
//            return "Success";
        }
        return "Fail";
    }
}

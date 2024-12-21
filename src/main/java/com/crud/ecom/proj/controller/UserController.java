package com.crud.ecom.proj.controller;


import com.crud.ecom.proj.model.Users;
import com.crud.ecom.proj.service.OTPService;
import com.crud.ecom.proj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    OTPService otpService;

    @PostMapping("/register")
    public Users registerUser(@RequestBody Users user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users user){
        return userService.verify(user);
    }

    @PostMapping("/sendOtp")
    public String sendOtp(@RequestBody String email){
        String decodedEmail = otpService.decodeEmail(email); // usually comes in abse64 @ as %40 so we have to decode
        System.out.println("decoded email --- > " + decodedEmail);
        return otpService.sendOTP(decodedEmail);
    }
}
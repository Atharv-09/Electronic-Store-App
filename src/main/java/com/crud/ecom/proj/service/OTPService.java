package com.crud.ecom.proj.service;

import com.crud.ecom.proj.model.Users;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Service
public class OTPService {

    String generatedOTP;

    @Autowired
    JavaMailSender mailSender;
    public String generateOTPForUser(Users user) {
        return generatedOTP;
    }

    public String sendOTP(String email) {
//      email = sanitizeEmail(email);
        generatedOTP = generate();
        sendOTPMail(email, generatedOTP);
        return "OTP sent to " + email;
    }
    private void sendOTPMail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("To register on Atharv's Elctronic Store, Your OTP code : " + otp);
        mailSender.send(message);
    }

    public String generate(){
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000); // generates a 6-digit OTP
        return String.valueOf(otp);
    }

    public String decodeEmail(String email) {
        // as = coming at the end of mail so mail is not triggered
        String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);
        // If the decoded email ends with '=', remove it
        if (decodedEmail.endsWith("=")) {
            decodedEmail = decodedEmail.substring(0, decodedEmail.length() - 1);
        }
        return decodedEmail;
    }

    /*// added extra code for sanitization of the entered mail
    public String sanitizeEmail(String email) {
        // Remove leading and trailing whitespaces
        email = email.trim();

        // Ensure no control characters or invalid symbols are present
        if (email.contains(" ") || email.contains("\n") || email.contains("\r")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        return email;
    }

    public boolean isValidEmail(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate(); // This will throw exception if email is invalid
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }*/

}

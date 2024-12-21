package com.crud.ecom.proj.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmationMail(String email, String orderId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Order Confirmation");
            message.setText("Thank you for your payment. Your order with ID " + orderId + " has been successfully placed.");

            mailSender.send(message);
            System.out.println("Order confirmation email sent to: " + email);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send email to: " + email);
        }
    }

}

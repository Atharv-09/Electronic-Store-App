package com.crud.ecom.proj.controller;
import com.crud.ecom.proj.model.PaymentData;
import com.crud.ecom.proj.model.Product;
import com.crud.ecom.proj.model.Users;
import com.crud.ecom.proj.repository.UserRepository;
import com.crud.ecom.proj.service.EmailService;
import com.crud.ecom.proj.service.JWTService;
import com.crud.ecom.proj.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin
public class PaymentController {
    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${razorpay.secret.key}")
    private String razorpaySecret;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private JWTService jwtService; // Custom JWT service for extracting user info

    @Autowired
    EmailService emailService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data,
                                         @RequestHeader("Authorization") String token) throws Exception {
        int amount = (int) data.get("amount");
        // Extract userId from token
        String username = jwtService.extractUserName(token.substring(7));
        Users user = userRepository.findByUsername(username);
//                .orElseThrow(() -> new RuntimeException("User not found"));

        Order razorpayOrder = paymentService.createRazorpayOrder(amount, user);
        return ResponseEntity.ok(razorpayOrder.toString());
    }

    @PostMapping("/update-status")
    public ResponseEntity<?> updatePaymentStatus(@RequestBody Map<String, Object> data) {
        String orderId = (String) data.get("orderId");
        String status = (String) data.get("status");

        paymentService.updatePaymentStatus(orderId, status);
        return ResponseEntity.ok("Payment status updated successfully");
    }

    @PostMapping("/verify")
    public String verifyPayment(@RequestBody PaymentData paymentData) {
        try {
            // Create Razorpay client
            RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);

            // Extract payment data sent from the frontend
            String paymentId = paymentData.getPayment_id();
            String orderId = paymentData.getOrder_id();
            String signature = paymentData.getSignature();
            String userEmail = paymentData.getEmail();

            // Verify the payment signature
            boolean isVerified = verifyPaymentSignature(paymentId, orderId, signature);

            if (isVerified) {
                emailService.sendOrderConfirmationMail(userEmail, orderId);
                return "Payment successfully verified!";
            } else {
                return "Payment verification failed!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while verifying payment.";
        }
    }

    private boolean verifyPaymentSignature(String paymentId, String orderId, String razorpaySignatureHex) throws Exception {
        // Concatenate order ID and payment ID
        String data = orderId + "|" + paymentId;
        System.out.println("Data for HMAC: " + data);

        // Generate HMAC-SHA256 signature
        String generatedSignatureBase64 = generateHmacSha256Signature(data, razorpaySecret);

        // Convert Razorpay's signature from hex to Base64
        String razorpaySignatureBase64 = hexToBase64(razorpaySignatureHex);

        // Compare the signatures
        return generatedSignatureBase64.equals(razorpaySignatureBase64);
    }

    private String generateHmacSha256Signature(String data, String secret) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes("UTF-8"));

        // Encode raw HMAC in Base64
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    private String hexToBase64(String hex) {
        byte[] rawBytes = new byte[hex.length() / 2];
        for (int i = 0; i < rawBytes.length; i++) {
            int index = i * 2;
            int j = Integer.parseInt(hex.substring(index, index + 2), 16);
            rawBytes[i] = (byte) j;
        }
        return Base64.getEncoder().encodeToString(rawBytes);
    }

}

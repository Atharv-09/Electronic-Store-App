package com.crud.ecom.proj.service;

import com.crud.ecom.proj.model.UserOrder;
import com.crud.ecom.proj.model.Users;
import com.crud.ecom.proj.repository.OrderRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${razorpay.secret.key}")
    private String razorpaySecret;

    @Autowired
    private OrderRepository orderRepository;

    public Order createRazorpayOrder(int amount, Users user) throws Exception {
        RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);

        JSONObject options = new JSONObject();
        options.put("amount", amount); // Razorpay accepts amounts in paise
        options.put("currency", "INR");
        options.put("receipt", "txn_" + System.currentTimeMillis());

        Order razorpayOrder = client.orders.create(options);

        // Save order to database
        UserOrder order = new UserOrder();
        order.setOrderId(razorpayOrder.get("id"));
        order.setAmount(amount);
        order.setPaymentStatus("Pending");
        order.setCreatedAt(new Date());
        order.setUser(user);

        orderRepository.save(order);

        return razorpayOrder;
    }

    public void updatePaymentStatus(String orderId, String status) {
        UserOrder order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setPaymentStatus(status);
        orderRepository.save(order);
    }
}

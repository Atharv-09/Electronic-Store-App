package com.crud.ecom.proj.repository;

import com.crud.ecom.proj.model.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<UserOrder, Integer> {
    Optional<UserOrder> findByOrderId(String orderId);
}


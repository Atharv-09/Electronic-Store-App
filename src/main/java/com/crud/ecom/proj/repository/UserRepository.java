package com.crud.ecom.proj.repository;

import com.crud.ecom.proj.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    public Users findByUsername(String username);
}

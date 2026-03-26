package com.example.paymentservice.repository;

import com.example.paymentservice.entity.IdempotencyStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IdempotencyRepository extends JpaRepository<IdempotencyStore, UUID> {
}

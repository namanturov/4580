package com.example.orderservice.repository;

import com.example.orderservice.entity.async.AsyncMessage;
import com.example.orderservice.entity.async.AsyncMessageId;
import com.example.orderservice.enums.AsyncMessageStatus;
import com.example.orderservice.enums.AsyncMessageType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsyncMessageRepository extends JpaRepository<AsyncMessage, AsyncMessageId> {
    List<AsyncMessage> findByStatusAndTypeOrderByCreatedAt(AsyncMessageStatus status,
                                                           AsyncMessageType type,
                                                           Pageable pageable);
}

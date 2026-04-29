package com.example.orderservice.repository.manager;

import com.example.orderservice.entity.async.AsyncMessage;
import com.example.orderservice.enums.AsyncMessageStatus;
import com.example.orderservice.enums.AsyncMessageType;
import com.example.orderservice.repository.AsyncMessageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AsyncMessageManager {

    AsyncMessageRepository repository;

    @Transactional
    public void save(AsyncMessage message) {
        repository.save(message);
    }

    public List<AsyncMessage> getUnsentOutboxMessages(Pageable pageable) {
        return repository.findByStatusAndTypeOrderByCreatedAt(
                AsyncMessageStatus.CREATED,
                AsyncMessageType.OUTBOX,
                pageable);
    }
}

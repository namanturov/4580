package com.example.orderservice.service.impl;

import com.example.orderservice.entity.async.AsyncMessage;
import com.example.orderservice.repository.manager.AsyncMessageManager;
import com.example.orderservice.service.AsyncMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AsyncMessageServiceImpl implements AsyncMessageService {

    AsyncMessageManager manager;

    @Override
    public void saveMessage(AsyncMessage message) {
        manager.save(message);
    }

    @Override
    public List<AsyncMessage> getUnsentOutboxMessages(int batchSize) {
        return manager.getUnsentOutboxMessages(Pageable.ofSize(batchSize));
    }
}

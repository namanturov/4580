package com.example.orderservice.service;

import com.example.orderservice.entity.async.AsyncMessage;

import java.util.List;

public interface AsyncMessageService {
    void saveMessage(AsyncMessage message);

    List<AsyncMessage> getUnsentOutboxMessages(int batchSize);
}

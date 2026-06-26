package com.abhisheksingh.loanaxisapi.feature.outbox.service;

import com.abhisheksingh.loanaxisapi.feature.outbox.entity.OutboxEvent;
import com.abhisheksingh.loanaxisapi.feature.outbox.enums.OutboxEventStatus;
import com.abhisheksingh.loanaxisapi.feature.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public OutboxEvent saveEvent(String eventType, String aggregateType, Long aggregateId, String payload) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .eventType(eventType)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(payload)
                .status(OutboxEventStatus.PENDING)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        return outboxEventRepository.save(outboxEvent);
    }
}

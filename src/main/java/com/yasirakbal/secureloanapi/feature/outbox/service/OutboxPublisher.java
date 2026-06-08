package com.yasirakbal.secureloanapi.feature.outbox.service;

import com.yasirakbal.secureloanapi.feature.outbox.entity.OutboxEvent;
import com.yasirakbal.secureloanapi.feature.outbox.enums.OutboxEventStatus;
import com.yasirakbal.secureloanapi.feature.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxEventRepository
                .findTop10ByStatusInOrderByCreatedAtAsc(
                        List.of(
                                OutboxEventStatus.PENDING,
                                OutboxEventStatus.FAILED
                        )
                );

        if (events.isEmpty()) {
            return;
        }

        for (OutboxEvent event : events) {
            processEvent(event);
        }
    }

    private void processEvent(OutboxEvent event) {
        try {
            event.setStatus(OutboxEventStatus.PROCESSING);
            outboxEventRepository.save(event);

            log.info(
                    "Processing outbox event. id={}, eventType={}, aggregateId={}",
                    event.getId(),
                    event.getEventType(),
                    event.getAggregateId()
            );

            // Actual email sending will be added in the next step.

            event.setStatus(OutboxEventStatus.SENT);
            event.setProcessedAt(LocalDateTime.now());
            event.setErrorMessage(null);

            outboxEventRepository.save(event);

            log.info(
                    "Outbox event sent successfully. id={}, eventType={}",
                    event.getId(),
                    event.getEventType()
            );

        } catch (Exception ex) {
            event.setStatus(OutboxEventStatus.FAILED);
            event.setRetryCount(event.getRetryCount() + 1);
            event.setErrorMessage(ex.getMessage());

            outboxEventRepository.save(event);

            log.error(
                    "Outbox event failed. id={}, eventType={}, error={}",
                    event.getId(),
                    event.getEventType(),
                    ex.getMessage()
            );
        }
    }
}
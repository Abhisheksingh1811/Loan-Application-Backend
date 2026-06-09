package com.yasirakbal.secureloanapi.feature.outbox.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yasirakbal.secureloanapi.feature.notification.dto.NotificationMessage;
import com.yasirakbal.secureloanapi.feature.notification.sender.NotificationSender;
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
    private final NotificationSender notificationSender;
    private final ObjectMapper objectMapper;

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

            NotificationMessage message = buildNotificationMessage(event);

            notificationSender.send(message);

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

    private NotificationMessage buildNotificationMessage(OutboxEvent event) throws Exception {
        JsonNode payload = objectMapper.readTree(event.getPayload());

        String customerEmail = payload.get("customerEmail").asText();
        String customerName = payload.get("customerName").asText();

        return switch (event.getEventType()) {
            case "LOAN_APPROVED", "LOAN_AUTO_APPROVED" -> buildLoanApprovedMessage(payload, customerEmail, customerName);
            case "LOAN_REJECTED", "LOAN_AUTO_REJECTED" -> buildLoanRejectedMessage(payload, customerEmail, customerName);
            default -> throw new IllegalArgumentException("Unsupported outbox event type: " + event.getEventType());
        };
    }

    private NotificationMessage buildLoanApprovedMessage(
            JsonNode payload,
            String customerEmail,
            String customerName
    ) {
        String subject = "Your SecureLoan Application Has Been Approved";

        String body = """
                Hello %s,

                Congratulations! Your loan application has been approved.

                Application ID: %s
                Loan ID: %s
                Loan Type: %s
                Approved Amount: %s
                Monthly Installment: %s
                Term: %s months

                Your loan is now active. Please make sure to pay installments on or before the due date.

                Regards,
                SecureLoan Team
                """.formatted(
                customerName,
                payload.get("applicationId").asText(),
                payload.has("loanId") ? payload.get("loanId").asText() : "N/A",
                payload.get("loanType").asText(),
                payload.get("approvedAmount").asText(),
                payload.get("monthlyInstallment").asText(),
                payload.get("termMonths").asText()
        );

        return NotificationMessage.builder()
                .to(customerEmail)
                .subject(subject)
                .body(body)
                .build();
    }

    private NotificationMessage buildLoanRejectedMessage(
            JsonNode payload,
            String customerEmail,
            String customerName
    ) {
        String subject = "Your SecureLoan Application Status";

        String body = """
                Hello %s,

                We regret to inform you that your loan application could not be approved.

                Application ID: %s
                Loan Type: %s
                Requested Amount: %s
                Reason: %s

                You may review your financial details and apply again later.

                Regards,
                SecureLoan Team
                """.formatted(
                customerName,
                payload.get("applicationId").asText(),
                payload.get("loanType").asText(),
                payload.get("requestedAmount").asText(),
                payload.get("rejectionReason").asText()
        );

        return NotificationMessage.builder()
                .to(customerEmail)
                .subject(subject)
                .body(body)
                .build();
    }
}
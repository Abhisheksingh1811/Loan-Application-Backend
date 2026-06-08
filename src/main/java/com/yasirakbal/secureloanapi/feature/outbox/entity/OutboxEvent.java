package com.yasirakbal.secureloanapi.feature.outbox.entity;

import com.yasirakbal.secureloanapi.feature.outbox.enums.OutboxEventStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private Long aggregateId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxEventStatus status;

    @Column(nullable = false)
    private Integer retryCount;

    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;
}
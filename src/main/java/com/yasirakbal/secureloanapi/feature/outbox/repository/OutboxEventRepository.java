package com.yasirakbal.secureloanapi.feature.outbox.repository;

import com.yasirakbal.secureloanapi.feature.outbox.entity.OutboxEvent;
import com.yasirakbal.secureloanapi.feature.outbox.enums.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop10ByStatusOrderByCreatedAtAsc(OutboxEventStatus status);

    List<OutboxEvent> findTop10ByStatusInOrderByCreatedAtAsc(List<OutboxEventStatus> statuses);
}
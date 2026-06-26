package com.abhisheksingh.loanaxisapi.feature.outbox.repository;

import com.abhisheksingh.loanaxisapi.feature.outbox.entity.OutboxEvent;
import com.abhisheksingh.loanaxisapi.feature.outbox.enums.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop10ByStatusOrderByCreatedAtAsc(OutboxEventStatus status);

    List<OutboxEvent> findTop10ByStatusInOrderByCreatedAtAsc(List<OutboxEventStatus> statuses);
}

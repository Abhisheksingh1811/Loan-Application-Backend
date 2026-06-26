package com.abhisheksingh.loanaxisapi.feature.notification.service;

import com.abhisheksingh.loanaxisapi.feature.notification.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.notification-exchange}")
    private String notificationExchange;

    @Value("${app.rabbitmq.notification-routing-key}")
    private String notificationRoutingKey;

    public void publish(NotificationMessage message) {

        rabbitTemplate.convertAndSend(
                notificationExchange,
                notificationRoutingKey,
                message
        );

        log.info(
                "Published notification to RabbitMQ. To={}, Subject={}",
                message.getTo(),
                message.getSubject()
        );
    }
}

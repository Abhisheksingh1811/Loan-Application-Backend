package com.yasirakbal.secureloanapi.feature.notification.consumer;

import com.yasirakbal.secureloanapi.feature.notification.dto.NotificationMessage;
import com.yasirakbal.secureloanapi.feature.notification.sender.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQNotificationConsumer {

    private final NotificationSender notificationSender;

    @RabbitListener(
            queues = "${app.rabbitmq.notification-queue}"
    )
    public void consume(NotificationMessage message) {

        log.info(
                "Received notification from RabbitMQ. To={}, Subject={}",
                message.getTo(),
                message.getSubject()
        );

        notificationSender.send(message);

        log.info(
                "Email sent successfully. To={}",
                message.getTo()
        );
    }
}
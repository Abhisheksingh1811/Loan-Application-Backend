package com.yasirakbal.secureloanapi.feature.notification.sender;

import com.yasirakbal.secureloanapi.feature.notification.dto.NotificationMessage;

public interface NotificationSender {

    void send(NotificationMessage message);
}
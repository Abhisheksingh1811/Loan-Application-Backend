package com.abhisheksingh.secureloannotification.notification.sender;

import com.abhisheksingh.secureloannotification.notification.dto.NotificationMessage;

public interface NotificationSender {

    void send(NotificationMessage message);
}
package com.abhisheksingh.loanaxisnotification.notification.sender;

import com.abhisheksingh.loanaxisnotification.notification.dto.NotificationMessage;

public interface NotificationSender {

    void send(NotificationMessage message);
}
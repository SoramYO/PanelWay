package com.minhtnn.panelway.api.services;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.minhtnn.panelway.utils.NotificationHelper;

public class PanelWayMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        NotificationHelper.createNotification(
                this,
                message.getNotification().getTitle(),
                message.getNotification().getBody()
        );
    }
}

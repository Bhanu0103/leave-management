package com.notification_service.service;

import com.notification_service.dto.NotificationRequest;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationServiceTest {

    @Test
    void sendNotification_ShouldAddToList() {
        NotificationService service = new NotificationService();
        NotificationRequest req = new NotificationRequest();
        req.setRecipient("user@test.com");
        req.setSubject("Subject");
        req.setBody("Body");
        req.setType("EMAIL");

        service.sendNotification(req);

        List<NotificationRequest> sent = service.getSentNotifications();
        assertEquals(1, sent.size());
        assertEquals("user@test.com", sent.get(0).getRecipient());
    }
}

package com.leave_service.service;

import com.leave_service.model.OutboxEvent;
import com.leave_service.repository.OutboxEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leave_service.dto.NotificationEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OutboxPublisher {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc("PENDING");
        for (OutboxEvent event : pendingEvents) {
            try {
                // The payload was serialized to JSON string in LeaveService
                // Convert it back to an object so JsonSerializer serializes it correctly instead of double-escaping a string.
                NotificationEvent payloadObj = objectMapper.readValue(event.getPayload(), NotificationEvent.class);
                kafkaTemplate.send(event.getTopic(), payloadObj);
                event.setStatus("PROCESSED");
                outboxEventRepository.save(event);
            } catch (Exception e) {
                // If Kafka is down, it throws exception, transaction rolls back (for this event? No, we need to catch it or let it fail).
                // If we let it throw, the transaction rolls back and it stays PENDING.
                throw new RuntimeException("Failed to send to Kafka", e);
            }
        }
    }
}

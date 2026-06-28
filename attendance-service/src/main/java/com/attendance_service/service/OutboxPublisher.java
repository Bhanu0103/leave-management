package com.attendance_service.service;

import com.attendance_service.model.OutboxEvent;
import com.attendance_service.repository.OutboxEventRepository;
import com.attendance_service.dto.NotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                NotificationEvent payloadObj = objectMapper.readValue(event.getPayload(), NotificationEvent.class);
                kafkaTemplate.send(event.getTopic(), payloadObj);
                event.setStatus("PROCESSED");
                outboxEventRepository.save(event);
            } catch (Exception e) {
                throw new RuntimeException("Failed to send to Kafka", e);
            }
        }
    }
}

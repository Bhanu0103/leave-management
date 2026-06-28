package com.attendance_service.service;

import com.attendance_service.model.Attendance;
import com.attendance_service.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.attendance_service.model.OutboxEvent;
import com.attendance_service.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.kafka.core.KafkaTemplate;
import com.attendance_service.dto.NotificationEvent;
import com.attendance_service.exception.BadRequestException;
import com.attendance_service.exception.ResourceNotFoundException;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final LocalTime LATE_THRESHOLD = LocalTime.of(9, 15);

    @Transactional
    public Attendance checkIn(Long userId) {
        LocalDate today = LocalDate.now();
        if (attendanceRepository.findByUserIdAndDate(userId, today).isPresent()) {
            throw new BadRequestException("Already checked in for today");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean late = now.toLocalTime().isAfter(LATE_THRESHOLD);

        Attendance attendance = new Attendance(userId, today, now, late);
        Attendance saved = attendanceRepository.save(attendance);

        // Publish event asynchronously via Outbox
        try {
            String subject = late ? "Late Check-In Flagged" : "Check-In Registered";
            String body = "Employee ID " + userId + " checked in at " + now.toLocalTime() + (late ? " (Late Arrival)" : " (On Time)");
            NotificationEvent event = new NotificationEvent("Employee_" + userId, subject, body);
            outboxEventRepository.save(new OutboxEvent("notification-topic", objectMapper.writeValueAsString(event)));
        } catch (Exception e) {
            // Log event sending failure but don't block check-in
        }

        return saved;
    }

    @Transactional
    public Attendance checkOut(Long userId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserIdAndDate(userId, today)
                .orElseThrow(() -> new ResourceNotFoundException("No check-in record found for today"));

        if (attendance.getCheckOut() != null) {
            throw new BadRequestException("Already checked out for today");
        }

        LocalDateTime now = LocalDateTime.now();
        attendance.setCheckOut(now);

        // Calculate hours
        Duration duration = Duration.between(attendance.getCheckIn(), now);
        double hours = duration.toMinutes() / 60.0;
        // round to 2 decimal places
        hours = Math.round(hours * 100.0) / 100.0;
        attendance.setWorkingHours(hours);

        Attendance saved = attendanceRepository.save(attendance);

        // Publish event asynchronously via Outbox
        try {
            String subject = "Check-Out Successful";
            String body = "Employee ID " + userId + " checked out at " + now.toLocalTime() + ". Worked hours: " + hours;
            NotificationEvent event = new NotificationEvent("Employee_" + userId, subject, body);
            outboxEventRepository.save(new OutboxEvent("notification-topic", objectMapper.writeValueAsString(event)));
        } catch (Exception e) {
            // Log event sending failure but don't block check-out
        }

        return saved;
    }

    public Attendance getTodayRecord(Long userId) {
        return attendanceRepository.findByUserIdAndDate(userId, LocalDate.now()).orElse(null);
    }

    public Page<Attendance> getUserHistory(Long userId, Pageable pageable) {
        return attendanceRepository.findByUserId(userId, pageable);
    }

    public List<Attendance> getAttendanceBetween(LocalDate start, LocalDate end) {
        return attendanceRepository.findByDateBetween(start, end);
    }

    public List<Attendance> getUserAttendanceBetween(Long userId, LocalDate start, LocalDate end) {
        return attendanceRepository.findByUserIdAndDateBetween(userId, start, end);
    }
}

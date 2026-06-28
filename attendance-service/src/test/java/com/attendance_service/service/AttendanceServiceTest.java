package com.attendance_service.service;

import com.attendance_service.exception.BadRequestException;
import com.attendance_service.model.Attendance;
import com.attendance_service.repository.AttendanceRepository;
import com.attendance_service.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AttendanceService attendanceService;

    @Test
    void checkIn_ShouldThrowException_WhenAlreadyCheckedIn() {
        when(attendanceRepository.findByUserIdAndDate(eq(1L), any(LocalDate.class)))
            .thenReturn(Optional.of(new Attendance()));
            
        assertThrows(BadRequestException.class, () -> attendanceService.checkIn(1L));
    }

    @Test
    void checkIn_ShouldSaveAttendance_WhenNotCheckedIn() {
        when(attendanceRepository.findByUserIdAndDate(eq(1L), any(LocalDate.class)))
            .thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(i -> i.getArgument(0));

        Attendance saved = attendanceService.checkIn(1L);
        
        assertNotNull(saved);
        assertEquals(1L, saved.getUserId());
        assertNotNull(saved.getCheckIn());
    }
}

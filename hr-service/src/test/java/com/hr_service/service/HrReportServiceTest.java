package com.hr_service.service;

import com.hr_service.client.AttendanceClient;
import com.hr_service.client.AuthClient;
import com.hr_service.client.LeaveClient;
import com.hr_service.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HrReportServiceTest {

    @Mock
    private AuthClient authClient;

    @Mock
    private AttendanceClient attendanceClient;

    @Mock
    private LeaveClient leaveClient;

    @InjectMocks
    private HrReportService hrReportService;

    @Test
    void getAttendanceReport_ShouldReturnReportForUser() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setUsername("testuser");

        AttendanceDto att = new AttendanceDto();
        att.setLate(true);
        att.setWorkingHours(8.0);

        when(authClient.getUserById(1L)).thenReturn(user);
        when(attendanceClient.getUserAttendanceBetween(eq(1L), anyString(), anyString())).thenReturn(Collections.singletonList(att));

        List<AttendanceReport> reports = hrReportService.getAttendanceReport(1L, LocalDate.now().minusDays(1), LocalDate.now());
        
        assertEquals(1, reports.size());
        assertEquals(1, reports.get(0).getLateCount());
        assertEquals(8.0, reports.get(0).getTotalHours());
    }
}

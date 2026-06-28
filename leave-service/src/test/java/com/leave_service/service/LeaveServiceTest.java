package com.leave_service.service;

import com.leave_service.exception.BadRequestException;
import com.leave_service.model.LeaveRequest;
import com.leave_service.model.LeaveStatus;
import com.leave_service.model.LeaveType;
import com.leave_service.repository.LeaveBalanceRepository;
import com.leave_service.repository.LeaveRequestRepository;
import com.leave_service.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Collections;
import java.util.Optional;
import com.leave_service.model.LeaveBalance;

@ExtendWith(MockitoExtension.class)
public class LeaveServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LeaveService leaveService;

    private LeaveRequest request;

    @BeforeEach
    void setUp() {
        request = new LeaveRequest();
        request.setLeaveType(LeaveType.CASUAL);
        request.setReason("Vacation");
    }

    @Test
    void applyLeave_ShouldSetPendingManager_WhenOnlyManagerIdPresent() {
        LeaveBalance mockBalance = new LeaveBalance(1L);
        mockBalance.setCasualLeave(10);
        when(leaveBalanceRepository.findByUserId(1L)).thenReturn(Optional.of(mockBalance));
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));
        request.setManagerId(2L);

        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveRequest saved = leaveService.applyLeave(1L, request);

        assertEquals(LeaveStatus.PENDING_MANAGER, saved.getStatus());
        assertEquals(1L, saved.getUserId());
    }

    @Test
    void applyLeave_ShouldThrowException_WhenEndDateBeforeStartDate() {
        request.setStartDate(LocalDate.now().plusDays(3));
        request.setEndDate(LocalDate.now().plusDays(1));

        assertThrows(BadRequestException.class, () -> leaveService.applyLeave(1L, request));
    }

    @Test
    void applyLeave_ShouldThrowException_WhenMedicalLeaveOver3DaysAndNoCertificate() {
        LeaveBalance mockBalance = new LeaveBalance(1L);
        mockBalance.setMedicalLeave(10);
        when(leaveBalanceRepository.findByUserId(1L)).thenReturn(Optional.of(mockBalance));

        request.setLeaveType(LeaveType.MEDICAL);
        request.setStartDate(LocalDate.of(2023, 10, 2)); // Monday
        request.setEndDate(LocalDate.of(2023, 10, 6)); // Friday (5 days)
        request.setManagerId(2L);
        
        when(leaveRequestRepository.findAllByUserId(1L)).thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> leaveService.applyLeave(1L, request));
    }

    @Test
    void applyLeave_ShouldAccept_WhenMedicalLeaveOver3DaysHasCertificate() {
        LeaveBalance mockBalance = new LeaveBalance(1L);
        mockBalance.setMedicalLeave(10);
        when(leaveBalanceRepository.findByUserId(1L)).thenReturn(Optional.of(mockBalance));

        request.setLeaveType(LeaveType.MEDICAL);
        request.setStartDate(LocalDate.of(2023, 10, 2)); // Monday
        request.setEndDate(LocalDate.of(2023, 10, 6)); // Friday (5 days)
        request.setManagerId(2L);
        request.setMedicalCertificate("http://drive.google.com/cert");

        when(leaveRequestRepository.findAllByUserId(1L)).thenReturn(Collections.emptyList());
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveRequest saved = leaveService.applyLeave(1L, request);

        assertEquals(LeaveStatus.PENDING_MANAGER, saved.getStatus());
    }
}

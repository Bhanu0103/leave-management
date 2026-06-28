package com.leave_service.repository;

import com.leave_service.model.LeaveRequest;
import com.leave_service.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    Page<LeaveRequest> findByUserId(Long userId, Pageable pageable);
    List<LeaveRequest> findAllByUserId(Long userId);
    List<LeaveRequest> findByManagerId(Long managerId);
    List<LeaveRequest> findByStatus(LeaveStatus status);
    Page<LeaveRequest> findByManagerIdAndStatus(Long managerId, LeaveStatus status, Pageable pageable);
    Page<LeaveRequest> findByHrIdAndStatus(Long hrId, LeaveStatus status, Pageable pageable);
}

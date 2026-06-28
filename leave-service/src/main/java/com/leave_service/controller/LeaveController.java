package com.leave_service.controller;

import com.leave_service.model.LeaveBalance;
import com.leave_service.model.LeaveRequest;
import com.leave_service.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.leave_service.security.UserPrincipal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody LeaveRequest request) {
        return ResponseEntity.ok(leaveService.applyLeave(principal.getId(), request));
    }

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<?> approveLeave(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserPrincipal principal) {
        if ("MANAGER".equalsIgnoreCase(principal.getRole())) {
            return ResponseEntity.ok(leaveService.approveManager(requestId, principal.getId()));
        } else if ("HR".equalsIgnoreCase(principal.getRole())) {
            return ResponseEntity.ok(leaveService.approveHR(requestId, principal.getId()));
        } else {
            throw new com.leave_service.exception.BadRequestException("Only Manager or HR can approve leave requests");
        }
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectLeave(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserPrincipal principal) {
        if ("MANAGER".equalsIgnoreCase(principal.getRole())) {
            return ResponseEntity.ok(leaveService.rejectManager(requestId, principal.getId()));
        } else if ("HR".equalsIgnoreCase(principal.getRole())) {
            return ResponseEntity.ok(leaveService.rejectHR(requestId, principal.getId()));
        } else {
            throw new com.leave_service.exception.BadRequestException("Only Manager or HR can reject leave requests");
        }
    }

    @PostMapping("/{requestId}/cancel")
    public ResponseEntity<?> cancelLeave(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(leaveService.cancelLeave(requestId, principal.getId()));
    }

    @GetMapping("/balance")
    public ResponseEntity<LeaveBalance> getMyBalance(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(leaveService.getOrCreateBalance(principal.getId()));
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<LeaveBalance> getUserBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(leaveService.getOrCreateBalance(userId));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<LeaveRequest>> getMyLeaves(@AuthenticationPrincipal UserPrincipal principal, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(leaveService.getUserLeaves(principal.getId(), pageable));
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests(@AuthenticationPrincipal UserPrincipal principal, @PageableDefault(size = 20) Pageable pageable) {
        if ("MANAGER".equalsIgnoreCase(principal.getRole())) {
            return ResponseEntity.ok(leaveService.getPendingManagerApprovals(principal.getId(), pageable));
        } else if ("HR".equalsIgnoreCase(principal.getRole())) {
            return ResponseEntity.ok(leaveService.getPendingHRApprovals(principal.getId(), pageable));
        } else {
            return ResponseEntity.badRequest().body("Only Managers and HR have pending approval dashboards");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Page<LeaveRequest>> getAllLeaves(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(leaveService.getAllLeaves(pageable));
    }

    @PostMapping("/escalate")
    public ResponseEntity<String> escalatePendingLeaves() {
        int escalated = leaveService.escalatePendingLeaves();
        return ResponseEntity.ok("Escalated " + escalated + " pending leave requests to HR");
    }

}

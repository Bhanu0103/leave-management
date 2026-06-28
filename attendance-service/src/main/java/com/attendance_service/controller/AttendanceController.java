package com.attendance_service.controller;

import com.attendance_service.model.Attendance;
import com.attendance_service.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.attendance_service.security.UserPrincipal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/checkin")
    public ResponseEntity<?> checkIn(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(attendanceService.checkIn(principal.getId()));
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkOut(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(attendanceService.checkOut(principal.getId()));
    }

    @GetMapping("/today")
    public ResponseEntity<Attendance> getTodayRecord(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(attendanceService.getTodayRecord(principal.getId()));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<Attendance>> getUserHistory(@AuthenticationPrincipal UserPrincipal principal, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(attendanceService.getUserHistory(principal.getId(), pageable));
    }

    @GetMapping("/range")
    public ResponseEntity<List<Attendance>> getAttendanceBetween(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(attendanceService.getAttendanceBetween(start, end));
    }

    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<Attendance>> getUserAttendanceBetween(
            @PathVariable Long userId,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(attendanceService.getUserAttendanceBetween(userId, start, end));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserAttendanceByAdmin(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        if (!"HR".equalsIgnoreCase(principal.getRole()) && !"MANAGER".equalsIgnoreCase(principal.getRole())) {
            return ResponseEntity.badRequest().body("Only HR and Managers can view other employees' attendance history");
        }
        return ResponseEntity.ok(attendanceService.getUserHistory(userId, pageable));
    }
}

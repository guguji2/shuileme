package com.slema.controller;

import com.slema.dto.ApiResponse;
import com.slema.dto.CheckInRequest;
import com.slema.dto.CheckInResponse;
import com.slema.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkin")
@CrossOrigin(origins = "*")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @PostMapping
    public ApiResponse<CheckInResponse> checkIn(@RequestBody CheckInRequest request) {
        try {
            CheckInResponse response = checkInService.checkIn(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/status/{userId}")
    public ApiResponse<Boolean> getCheckInStatus(@PathVariable Long userId) {
        try {
            boolean hasCheckedIn = checkInService.hasCheckedInToday(userId);
            return ApiResponse.success(hasCheckedIn);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

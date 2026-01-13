package com.slema.controller;

import com.slema.dto.ApiResponse;
import com.slema.entity.EmergencyContact;
import com.slema.entity.SleepSetting;
import com.slema.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class SettingController {

    @Autowired
    private SettingService settingService;

    @PostMapping("/sleep")
    public ApiResponse<SleepSetting> saveSleepSetting(@RequestBody SleepSettingRequest request) {
        try {
            SleepSetting setting = settingService.saveSleepSetting(
                request.getUserId(), 
                request.getDeadlineTime(), 
                request.getReminderMinutes()
            );
            return ApiResponse.success(setting);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/sleep/{userId}")
    public ApiResponse<SleepSetting> getSleepSetting(@PathVariable Long userId) {
        try {
            SleepSetting setting = settingService.getSleepSetting(userId);
            return ApiResponse.success(setting);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/contacts")
    public ApiResponse<EmergencyContact> addEmergencyContact(@RequestBody ContactRequest request) {
        try {
            EmergencyContact contact = settingService.addEmergencyContact(
                request.getUserId(),
                request.getName(),
                request.getEmail()
            );
            return ApiResponse.success(contact);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/contacts/{userId}")
    public ApiResponse<List<EmergencyContact>> getEmergencyContacts(@PathVariable Long userId) {
        try {
            List<EmergencyContact> contacts = settingService.getEmergencyContacts(userId);
            return ApiResponse.success(contacts);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/contacts/{contactId}")
    public ApiResponse<Void> deleteEmergencyContact(@PathVariable Long contactId) {
        try {
            settingService.deleteEmergencyContact(contactId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 内部DTO类
    public static class SleepSettingRequest {
        private Long userId;
        private String deadlineTime;
        private Integer reminderMinutes;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getDeadlineTime() { return deadlineTime; }
        public void setDeadlineTime(String deadlineTime) { this.deadlineTime = deadlineTime; }
        public Integer getReminderMinutes() { return reminderMinutes; }
        public void setReminderMinutes(Integer reminderMinutes) { this.reminderMinutes = reminderMinutes; }
    }

    public static class ContactRequest {
        private Long userId;
        private String name;
        private String email;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}

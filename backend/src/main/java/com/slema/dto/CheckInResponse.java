package com.slema.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CheckInResponse {
    private Long id;
    private Long userId;
    private LocalDateTime checkInTime;
    private String message;
}

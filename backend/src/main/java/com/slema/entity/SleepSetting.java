package com.slema.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sleep_setting")
public class SleepSetting {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String deadlineTime;

    private Integer reminderMinutes;

    private LocalDateTime createdAt;
}

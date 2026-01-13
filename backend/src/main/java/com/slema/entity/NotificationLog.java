package com.slema.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("notification_log")
public class NotificationLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long contactId;

    private LocalDateTime sentTime;

    private String status;

    private LocalDateTime createdAt;
}

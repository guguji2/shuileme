package com.slema.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("emergency_contact")
public class EmergencyContact {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String email;

    private LocalDateTime createdAt;
}

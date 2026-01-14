package com.slema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 月度热力图数据响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyHeatmapResponse {
    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份 (1-12)
     */
    private Integer month;

    /**
     * 每日打卡状态 (key: 日期 1-31, value: 是否已打卡)
     */
    private Map<Integer, Boolean> days;

    /**
     * 当月打卡总天数
     */
    private Integer totalDays;
}

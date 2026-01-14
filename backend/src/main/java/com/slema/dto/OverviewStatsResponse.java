package com.slema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 概览统计数据响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverviewStatsResponse {
    /**
     * 累计打卡天数
     */
    private Integer totalDays;

    /**
     * 当前连续打卡天数
     */
    private Integer currentStreak;

    /**
     * 本月打卡天数
     */
    private Integer monthlyDays;

    /**
     * 最长连续打卡天数
     */
    private Integer longestStreak;
}

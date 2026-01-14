package com.slema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 周统计数据响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyStatsResponse {
    /**
     * 最近 7 天日期列表 (格式: MM-dd)
     */
    private List<String> dates;

    /**
     * 每日打卡次数列表 (0 或 1)
     */
    private List<Integer> counts;

    /**
     * 每日打卡时间列表 (格式: HH:mm，未打卡为 null)
     */
    private List<String> checkInTimes;
}

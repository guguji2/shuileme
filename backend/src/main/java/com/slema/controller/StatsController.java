package com.slema.controller;

import com.slema.dto.ApiResponse;
import com.slema.dto.MonthlyHeatmapResponse;
import com.slema.dto.OverviewStatsResponse;
import com.slema.dto.WeeklyStatsResponse;
import com.slema.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 统计数据控制器
 */
@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {

    @Autowired
    private StatsService statsService;

    /**
     * 获取概览统计数据
     * @param userId 用户ID
     * @return 概览统计响应
     */
    @GetMapping("/overview/{userId}")
    public ApiResponse<OverviewStatsResponse> getOverviewStats(@PathVariable Long userId) {
        try {
            OverviewStatsResponse response = statsService.getOverviewStats(userId);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取周统计数据
     * @param userId 用户ID
     * @return 周统计响应
     */
    @GetMapping("/weekly/{userId}")
    public ApiResponse<WeeklyStatsResponse> getWeeklyStats(@PathVariable Long userId) {
        try {
            WeeklyStatsResponse response = statsService.getWeeklyStats(userId);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取月度热力图数据
     * @param userId 用户ID
     * @param year 年份（可选，默认当前年份）
     * @param month 月份（可选，默认当前月份）
     * @return 月度热力图响应
     */
    @GetMapping("/monthly/{userId}")
    public ApiResponse<MonthlyHeatmapResponse> getMonthlyHeatmap(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        try {
            // 默认使用当前年月
            LocalDate now = LocalDate.now();
            int actualYear = year != null ? year : now.getYear();
            int actualMonth = month != null ? month : now.getMonthValue();

            MonthlyHeatmapResponse response = statsService.getMonthlyHeatmap(userId, actualYear, actualMonth);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

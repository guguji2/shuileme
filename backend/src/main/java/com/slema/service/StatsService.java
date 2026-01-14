package com.slema.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.slema.dto.MonthlyHeatmapResponse;
import com.slema.dto.OverviewStatsResponse;
import com.slema.dto.WeeklyStatsResponse;
import com.slema.entity.CheckIn;
import com.slema.mapper.CheckInMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务
 */
@Service
public class StatsService {

    @Autowired
    private CheckInMapper checkInMapper;

    /**
     * 获取概览统计数据
     * @param userId 用户ID
     * @return 概览统计响应
     */
    public OverviewStatsResponse getOverviewStats(Long userId) {
        // 查询所有打卡记录
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getUserId, userId)
               .orderByDesc(CheckIn::getDate);
        List<CheckIn> allCheckIns = checkInMapper.selectList(wrapper);

        // 累计打卡天数
        int totalDays = allCheckIns.size();

        // 计算当前连续打卡天数
        int currentStreak = calculateCurrentStreak(allCheckIns);

        // 计算最长连续打卡天数
        int longestStreak = calculateLongestStreak(allCheckIns);

        // 计算本月打卡天数
        LocalDate now = LocalDate.now();
        int monthlyDays = (int) allCheckIns.stream()
                .filter(checkIn -> checkIn.getDate().getYear() == now.getYear()
                        && checkIn.getDate().getMonthValue() == now.getMonthValue())
                .count();

        OverviewStatsResponse response = new OverviewStatsResponse();
        response.setTotalDays(totalDays);
        response.setCurrentStreak(currentStreak);
        response.setMonthlyDays(monthlyDays);
        response.setLongestStreak(longestStreak);
        return response;
    }

    /**
     * 获取周统计数据
     * @param userId 用户ID
     * @return 周统计响应
     */
    public WeeklyStatsResponse getWeeklyStats(Long userId) {
        // 计算最近 7 天的日期范围
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);

        // 查询最近 7 天的打卡记录
        List<CheckIn> weeklyCheckIns = checkInMapper.findWeeklyCheckIns(
                userId,
                sevenDaysAgo.toString()
        );

        // 构建日期和打卡状态映射
        Map<LocalDate, CheckIn> checkInMap = weeklyCheckIns.stream()
                .collect(Collectors.toMap(CheckIn::getDate, checkIn -> checkIn));

        // 构建返回数据
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        List<String> checkInTimes = new ArrayList<>();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i < 7; i++) {
            LocalDate date = sevenDaysAgo.plusDays(i);
            dates.add(date.format(dateFormatter));

            CheckIn checkIn = checkInMap.get(date);
            if (checkIn != null) {
                counts.add(1);
                checkInTimes.add(checkIn.getCheckInTime().format(timeFormatter));
            } else {
                counts.add(0);
                checkInTimes.add(null);
            }
        }

        WeeklyStatsResponse response = new WeeklyStatsResponse();
        response.setDates(dates);
        response.setCounts(counts);
        response.setCheckInTimes(checkInTimes);
        return response;
    }

    /**
     * 获取月度热力图数据
     * @param userId 用户ID
     * @param year 年份
     * @param month 月份
     * @return 月度热力图响应
     */
    public MonthlyHeatmapResponse getMonthlyHeatmap(Long userId, int year, int month) {
        // 查询指定月份的打卡记录
        List<CheckIn> monthlyCheckIns = checkInMapper.findMonthlyCheckIns(userId, year, month);

        // 构建日期打卡状态映射
        Map<Integer, Boolean> daysMap = new HashMap<>();
        for (CheckIn checkIn : monthlyCheckIns) {
            int day = checkIn.getDate().getDayOfMonth();
            daysMap.put(day, true);
        }

        // 初始化当月所有日期为 false
        LocalDate date = LocalDate.of(year, month, 1);
        int daysInMonth = date.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            daysMap.putIfAbsent(day, false);
        }

        MonthlyHeatmapResponse response = new MonthlyHeatmapResponse();
        response.setYear(year);
        response.setMonth(month);
        response.setDays(daysMap);
        response.setTotalDays(monthlyCheckIns.size());
        return response;
    }

    /**
     * 计算当前连续打卡天数
     * @param checkIns 打卡记录列表（按日期降序）
     * @return 连续打卡天数
     */
    private int calculateCurrentStreak(List<CheckIn> checkIns) {
        if (checkIns.isEmpty()) {
            return 0;
        }

        // 确保按日期降序排序
        checkIns.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate expectedDate = today;

        for (CheckIn checkIn : checkIns) {
            if (checkIn.getDate().equals(expectedDate)) {
                streak++;
                expectedDate = expectedDate.minusDays(1);
            } else if (checkIn.getDate().isBefore(expectedDate.minusDays(1))) {
                // 遇到中断，停止计算
                break;
            }
        }

        return streak;
    }

    /**
     * 计算最长连续打卡天数
     * @param checkIns 打卡记录列表
     * @return 最长连续打卡天数
     */
    private int calculateLongestStreak(List<CheckIn> checkIns) {
        if (checkIns.isEmpty()) {
            return 0;
        }

        // 按日期升序排序
        checkIns.sort(Comparator.comparing(CheckIn::getDate));

        int longestStreak = 1;
        int currentStreak = 1;

        for (int i = 1; i < checkIns.size(); i++) {
            LocalDate prevDate = checkIns.get(i - 1).getDate();
            LocalDate currentDate = checkIns.get(i).getDate();

            if (currentDate.equals(prevDate.plusDays(1))) {
                currentStreak++;
                longestStreak = Math.max(longestStreak, currentStreak);
            } else if (!currentDate.equals(prevDate)) {
                // 日期不连续，重置当前连续天数
                currentStreak = 1;
            }
        }

        return checkIns.size() == 1 ? 1 : longestStreak;
    }
}

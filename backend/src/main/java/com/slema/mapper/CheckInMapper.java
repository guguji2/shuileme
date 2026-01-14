package com.slema.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slema.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {

    /**
     * 查询用户指定日期范围的打卡记录（周统计）
     * @param userId 用户ID
     * @param startDate 开始日期
     * @return 打卡记录列表
     */
    List<CheckIn> findWeeklyCheckIns(@Param("userId") Long userId, @Param("startDate") String startDate);

    /**
     * 查询用户指定年月的打卡记录（月度热力图）
     * @param userId 用户ID
     * @param year 年份
     * @param month 月份
     * @return 打卡记录列表
     */
    List<CheckIn> findMonthlyCheckIns(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);
}

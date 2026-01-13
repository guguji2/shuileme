package com.slema.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.slema.dto.CheckInRequest;
import com.slema.dto.CheckInResponse;
import com.slema.entity.CheckIn;
import com.slema.mapper.CheckInMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CheckInService {

    @Autowired
    private CheckInMapper checkInMapper;

    public CheckInResponse checkIn(CheckInRequest request) {
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getUserId, request.getUserId())
               .eq(CheckIn::getDate, today);

        CheckIn existing = checkInMapper.selectOne(wrapper);
        if (existing != null) {
            CheckInResponse response = new CheckInResponse();
            response.setUserId(existing.getUserId());
            response.setCheckInTime(existing.getCheckInTime());
            response.setMessage("今日已打卡");
            return response;
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setUserId(request.getUserId());
        checkIn.setCheckInTime(LocalDateTime.now());
        checkIn.setDate(today);
        checkIn.setCreatedAt(LocalDateTime.now());

        checkInMapper.insert(checkIn);

        CheckInResponse response = new CheckInResponse();
        response.setId(checkIn.getId());
        response.setUserId(checkIn.getUserId());
        response.setCheckInTime(checkIn.getCheckInTime());
        response.setMessage("打卡成功");
        return response;
    }

    public boolean hasCheckedInToday(Long userId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getUserId, userId)
               .eq(CheckIn::getDate, today);
        return checkInMapper.selectCount(wrapper) > 0;
    }
}

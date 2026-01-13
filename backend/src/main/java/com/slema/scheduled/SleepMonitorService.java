package com.slema.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.slema.entity.CheckIn;
import com.slema.entity.EmergencyContact;
import com.slema.entity.NotificationLog;
import com.slema.entity.SleepSetting;
import com.slema.entity.User;
import com.slema.mapper.CheckInMapper;
import com.slema.mapper.EmergencyContactMapper;
import com.slema.mapper.NotificationLogMapper;
import com.slema.mapper.SleepSettingMapper;
import com.slema.mapper.UserMapper;
import com.slema.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class SleepMonitorService {

    @Autowired
    private SleepSettingMapper sleepSettingMapper;

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmergencyContactMapper emergencyContactMapper;

    @Autowired
    private NotificationLogMapper notificationLogMapper;

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedDelayString = "${app.monitor.check-interval}")
    public void monitorSleepCheckIn() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        LocalDate today = now.toLocalDate();

        LambdaQueryWrapper<SleepSetting> settingWrapper = new LambdaQueryWrapper<>();
        List<SleepSetting> settings = sleepSettingMapper.selectList(settingWrapper);

        for (SleepSetting setting : settings) {
            try {
                LocalTime deadline = LocalTime.parse(setting.getDeadlineTime());

                if (currentTime.isAfter(deadline)) {
                    LambdaQueryWrapper<CheckIn> checkInWrapper = new LambdaQueryWrapper<>();
                    checkInWrapper.eq(CheckIn::getUserId, setting.getUserId())
                                  .eq(CheckIn::getDate, today);

                    CheckIn checkIn = checkInMapper.selectOne(checkInWrapper);

                    if (checkIn == null) {
                        LambdaQueryWrapper<NotificationLog> logWrapper = new LambdaQueryWrapper<>();
                        logWrapper.eq(NotificationLog::getUserId, setting.getUserId())
                                 .eq(NotificationLog::getStatus, "SUCCESS")
                                 .apply("DATE(sent_time) = {0}", today);

                        long alreadyNotified = notificationLogMapper.selectCount(logWrapper);

                        if (alreadyNotified == 0) {
                            User user = userMapper.selectById(setting.getUserId());
                            LambdaQueryWrapper<EmergencyContact> contactWrapper = new LambdaQueryWrapper<>();
                            contactWrapper.eq(EmergencyContact::getUserId, setting.getUserId());
                            List<EmergencyContact> contacts = emergencyContactMapper.selectList(contactWrapper);

                            if (!contacts.isEmpty()) {
                                emailService.sendMissedSleepNotification(user, contacts);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

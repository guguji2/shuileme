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
        System.out.println("=== 定时任务执行: 检查睡眠打卡 ===");
        System.out.println("当前时间: " + LocalDateTime.now());

        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        LocalDate today = now.toLocalDate();

        LambdaQueryWrapper<SleepSetting> settingWrapper = new LambdaQueryWrapper<>();
        List<SleepSetting> settings = sleepSettingMapper.selectList(settingWrapper);
        System.out.println("找到 " + settings.size() + " 条睡眠设置");

        for (SleepSetting setting : settings) {
            try {
                // 获取用户信息并检查状态
                User user = userMapper.selectById(setting.getUserId());
                if (user == null || !"ACTIVE".equals(user.getStatus())) {
                    System.out.println("用户ID: " + setting.getUserId() + " → 用户不存在或已退出登录，跳过");
                    continue;
                }

                LocalTime deadline = LocalTime.parse(setting.getDeadlineTime());
                System.out.println("用户ID: " + setting.getUserId() + ", 截止时间: " + deadline + ", 当前时间: " + currentTime);

                if (currentTime.isAfter(deadline)) {
                    System.out.println("  → 已超时，检查是否打卡");
                    LambdaQueryWrapper<CheckIn> checkInWrapper = new LambdaQueryWrapper<>();
                    checkInWrapper.eq(CheckIn::getUserId, setting.getUserId())
                                  .eq(CheckIn::getDate, today);

                    CheckIn checkIn = checkInMapper.selectOne(checkInWrapper);

                    if (checkIn == null) {
                        System.out.println("  → 未打卡，检查是否已通知");
                        LambdaQueryWrapper<NotificationLog> logWrapper = new LambdaQueryWrapper<>();
                        logWrapper.eq(NotificationLog::getUserId, setting.getUserId())
                                 .eq(NotificationLog::getStatus, "SUCCESS")
                                 .apply("DATE(sent_time) = {0}", today);

                        long alreadyNotified = notificationLogMapper.selectCount(logWrapper);
                        System.out.println("  → 今日已通知次数: " + alreadyNotified);

                        if (alreadyNotified == 0) {
                            System.out.println("  → 准备发送邮件通知");
                            LambdaQueryWrapper<EmergencyContact> contactWrapper = new LambdaQueryWrapper<>();
                            contactWrapper.eq(EmergencyContact::getUserId, setting.getUserId());
                            List<EmergencyContact> contacts = emergencyContactMapper.selectList(contactWrapper);

                            System.out.println("  → 找到 " + contacts.size() + " 个紧急联系人");

                            if (!contacts.isEmpty()) {
                                emailService.sendMissedSleepNotification(user, contacts);
                            } else {
                                System.out.println("  → 无紧急联系人，跳过");
                            }
                        } else {
                            System.out.println("  → 今日已发送过通知，跳过");
                        }
                    } else {
                        System.out.println("  → 已打卡，无需通知");
                    }
                } else {
                    System.out.println("  → 未超时");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

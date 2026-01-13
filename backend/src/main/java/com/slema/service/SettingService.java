package com.slema.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.slema.entity.SleepSetting;
import com.slema.entity.EmergencyContact;
import com.slema.mapper.SleepSettingMapper;
import com.slema.mapper.EmergencyContactMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SettingService {

    @Autowired
    private SleepSettingMapper sleepSettingMapper;

    @Autowired
    private EmergencyContactMapper emergencyContactMapper;

    public SleepSetting saveSleepSetting(Long userId, String deadlineTime, Integer reminderMinutes) {
        LambdaQueryWrapper<SleepSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SleepSetting::getUserId, userId);
        SleepSetting existing = sleepSettingMapper.selectOne(wrapper);

        if (existing != null) {
            existing.setDeadlineTime(deadlineTime);
            existing.setReminderMinutes(reminderMinutes);
            sleepSettingMapper.updateById(existing);
            return existing;
        } else {
            SleepSetting setting = new SleepSetting();
            setting.setUserId(userId);
            setting.setDeadlineTime(deadlineTime);
            setting.setReminderMinutes(reminderMinutes);
            setting.setCreatedAt(LocalDateTime.now());
            sleepSettingMapper.insert(setting);
            return setting;
        }
    }

    public SleepSetting getSleepSetting(Long userId) {
        LambdaQueryWrapper<SleepSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SleepSetting::getUserId, userId);
        return sleepSettingMapper.selectOne(wrapper);
    }

    public EmergencyContact addEmergencyContact(Long userId, String name, String email) {
        EmergencyContact contact = new EmergencyContact();
        contact.setUserId(userId);
        contact.setName(name);
        contact.setEmail(email);
        contact.setCreatedAt(LocalDateTime.now());
        emergencyContactMapper.insert(contact);
        return contact;
    }

    public List<EmergencyContact> getEmergencyContacts(Long userId) {
        LambdaQueryWrapper<EmergencyContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyContact::getUserId, userId);
        return emergencyContactMapper.selectList(wrapper);
    }

    public void deleteEmergencyContact(Long contactId) {
        emergencyContactMapper.deleteById(contactId);
    }
}

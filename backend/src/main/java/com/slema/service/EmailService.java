package com.slema.service;

import com.slema.entity.EmergencyContact;
import com.slema.entity.NotificationLog;
import com.slema.entity.User;
import com.slema.mapper.NotificationLogMapper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationLogMapper notificationLogMapper;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.from-name:睡了么}")
    private String fromName;

    public void sendMissedSleepNotification(User user, List<EmergencyContact> contacts) {
        for (EmergencyContact contact : contacts) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(fromEmail, fromName);
                helper.setTo(contact.getEmail());
                helper.setSubject("【睡了么】" + user.getUsername() + " 未按时睡觉提醒");

                String content = buildEmailContent(user, contact);
                helper.setText(content, false);

                mailSender.send(message);

                logNotification(user.getId(), contact.getId(), "SUCCESS");
                System.out.println("✅ 邮件发送成功: " + contact.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
                logNotification(user.getId(), contact.getId(), "FAILED");
                System.err.println("❌ 邮件发送失败: " + contact.getEmail() + ", 错误: " + e.getMessage());
            }
        }
    }

    private String buildEmailContent(User user, EmergencyContact contact) {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("           ⏰ 睡眠提醒通知\n");
        sb.append("==================================================\n\n");
        sb.append("您好 ").append(contact.getName()).append("，\n\n");
        sb.append("您的朋友 ").append(user.getUsername()).append(" 今晚未能按时打卡睡觉。\n\n");
        sb.append("建议您提醒他/她早点休息，保持良好的作息习惯。\n\n");
        sb.append("--------------------------------------------------\n");
        sb.append("        😴 早睡早起身体好\n");
        sb.append("--------------------------------------------------\n\n");
        sb.append("这是一封自动发送的邮件，请勿直接回复。\n\n");
        sb.append("—— 睡了么 App 💤\n");
        sb.append("==================================================\n");
        
        return sb.toString();
    }

    private void logNotification(Long userId, Long contactId, String status) {
        NotificationLog log = new NotificationLog();
        log.setUserId(userId);
        log.setContactId(contactId);
        log.setSentTime(LocalDateTime.now());
        log.setStatus(status);
        log.setCreatedAt(LocalDateTime.now());
        notificationLogMapper.insert(log);
    }
}

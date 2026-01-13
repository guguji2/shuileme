package com.slema.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.slema.config.JwtUtil;
import com.slema.entity.User;
import com.slema.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(String username, String phone, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User existing = userMapper.selectOne(wrapper);
        if (existing != null) {
            throw new RuntimeException("手机号已注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPassword(password);
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insert(user);

        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }

    public String login(String phone, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }

    public User getUserByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return userMapper.selectOne(wrapper);
    }
}

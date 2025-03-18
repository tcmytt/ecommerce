package io.github.tcmytt.ecommerce.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Component
public class OtpUtil {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int OTP_LENGTH = 8; // Độ dài OTP

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Tạo OTP ngẫu nhiên
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return otp.toString();
    }

    // Lưu OTP vào Redis với thời gian hết hạn
    public void saveOtp(String email, String otp, long expirationInSeconds) {
        String key = "otp:" + email;
        redisTemplate.opsForValue().set(key, otp);
        redisTemplate.expire(key, expirationInSeconds, TimeUnit.SECONDS); // Hết hạn sau X giây
    }

    // Lấy OTP từ Redis
    public String getOtp(String email) {
        String key = "otp:" + email;
        return (String) redisTemplate.opsForValue().get(key);
    }

    // Xóa OTP khỏi Redis
    public void deleteOtp(String email) {
        String key = "otp:" + email;
        redisTemplate.delete(key);
    }
}
package io.github.tcmytt.ecommerce.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Lưu dữ liệu vào Redis với thời gian hết hạn
    public void saveToCache(String key, Object value, long expirationInSeconds) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, expirationInSeconds, TimeUnit.SECONDS); // Hết hạn sau X giây
    }

    // Lấy dữ liệu từ Redis
    public Object getFromCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Xóa dữ liệu khỏi Redis
    public void deleteFromCache(String key) {
        redisTemplate.delete(key);
    }

    // Kiểm tra xem key có tồn tại trong Redis không
    public boolean exists(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }
}
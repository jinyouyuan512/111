package com.jiyi.mall.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单号生成器
 */
public class OrderNumberGenerator {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();
    
    /**
     * 生成订单号
     * 格式: yyyyMMddHHmmss + 6位随机数
     */
    public static String generate() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        int randomNum = RANDOM.nextInt(900000) + 100000; // 6位随机数
        return timestamp + randomNum;
    }
}

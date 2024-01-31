package com.nwnu.shortlink.admin.common.constant;

/**
 * 短连接后台，Redis 常量缓存
 */
public class RedisCacheConstant {

    // 用户注册 分布式锁
    public static final String LOCK_USER_REGISTER_KEY = "short-link:lock_user-register:";
}

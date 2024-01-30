package com.nwnu.shortlink.admin.config;


import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 布隆过滤器配置
 */
@Configuration
public class RBloomFilterConfiguration {

    /**
     * 防止用户注册查询数据库的布隆过滤器
     */

    @Bean
    public RBloomFilter<String> userRegisterCachePenetrationBloomFilters(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("userRegisterCachePenetrationBloomFilters");
        cachePenetrationBloomFilter.tryInit(100000000L, 0.001); // 一个是预估元素大小，一个是误判率
        return cachePenetrationBloomFilter;
    }
}
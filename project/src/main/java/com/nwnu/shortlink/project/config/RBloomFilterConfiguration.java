package com.nwnu.shortlink.project.config;


import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 布隆过滤器配置
 */
@Configuration
public class RBloomFilterConfiguration {

    /**
     * 防止短链接创建过滤器
     */

    @Bean
    public RBloomFilter<String> shortLinkCreatCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("userRegisterCachePenetrationBloomFilters");
        cachePenetrationBloomFilter.tryInit(100000000L, 0.001); // 一个是预估元素大小，一个是误判率
        return cachePenetrationBloomFilter;
    }
}
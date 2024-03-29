package com.nwnu.shortlink.admin.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwnu.shortlink.admin.common.convention.expection.ClientException;
import com.nwnu.shortlink.admin.common.enums.UserErrorCode;
import com.nwnu.shortlink.admin.dao.entity.UserDo;
import com.nwnu.shortlink.admin.dao.mapper.UserMapper;
import com.nwnu.shortlink.admin.dto.req.UserLoginReqDto;
import com.nwnu.shortlink.admin.dto.req.UserRegisterReqDto;
import com.nwnu.shortlink.admin.dto.req.UserUpdateReqDto;
import com.nwnu.shortlink.admin.dto.resp.UserLoginRespDto;
import com.nwnu.shortlink.admin.dto.resp.UserRespDTO;
import com.nwnu.shortlink.admin.service.GroupService;
import com.nwnu.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.nwnu.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.nwnu.shortlink.admin.common.enums.UserErrorCode.*;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    // 这个用于对注册用户加锁
    private final RedissonClient redissonClient;
    // 将登录信息存储在redis中
    private final StringRedisTemplate stringRedisTemplate;
    private final GroupService groupService;




    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.lambdaQuery(UserDo.class).eq(UserDo::getUsername, username);
        UserDo userDo = baseMapper.selectOne(queryWrapper);
        if (userDo == null){
            throw new ClientException(UserErrorCode.USER_NULL_CODE);
        }
        UserRespDTO userRespDTO = new UserRespDTO();
        BeanUtils.copyProperties(userDo, userRespDTO);
        return userRespDTO;
    }


    @Override
    public Boolean hasUsername(String username) {
        // 第一版构造一个表达式从数据库里面查询
//        LambdaQueryWrapper<UserDo> queryWrapper = Wrappers.lambdaQuery(UserDo.class).eq(UserDo::getUsername, username);
//        UserDo userDo = baseMapper.selectOne(queryWrapper);
//        return userDo == null;
        return !userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDto requestParam) {
        // 首先要判断username是否存在
//        if (!hasUsername(requestParam.getUsername())){
//            throw new ClientException(USER_NAME_EXIST_CODE);
//        }
        // 对当前用户名加锁
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());

        try {
            if (lock.tryLock()) {
                try {
                    int insert = baseMapper.insert(BeanUtil.copyProperties(requestParam, UserDo.class));
                    if (insert < 1){
                        throw new ClientException(USER_SAVE_CODE);
                    }
                } catch (DuplicateKeyException e){
                    throw new ClientException(USER_EXIST_CODE);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                groupService.saveGroup(requestParam.getUsername(), "默认分组");
                return;
            }
            throw new ClientException(USER_NAME_EXIST_CODE);
        }finally {
            lock.unlock();
        }

    }

    @Override
    public void updateUser(UserUpdateReqDto userUpdateReqDto) {
        // TODO 验证当前用户是够为登录用户

        LambdaQueryWrapper<UserDo> wrapper = Wrappers.lambdaQuery(UserDo.class).eq(UserDo::getUsername, userUpdateReqDto.getUsername());
        baseMapper.update(BeanUtil.copyProperties(userUpdateReqDto, UserDo.class), wrapper);
    }

    /**
     * 用户登录接口
     * @param userLoginReqDto
     * @return
     */
    @Override
    public UserLoginRespDto login(UserLoginReqDto userLoginReqDto) {
        // 首先验证用户名是否正确
        LambdaQueryWrapper<UserDo> wrapper = Wrappers.lambdaQuery(UserDo.class).eq(UserDo::getUsername, userLoginReqDto.getUsername())
                .eq(UserDo::getPassword, userLoginReqDto.getPassword())
                .eq(UserDo::getDelFlag, 0)
                ;

        UserDo userDo = baseMapper.selectOne(wrapper);
        if (userDo == null){
            throw new ClientException(USER_NULL_CODE);
        }
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey("login_" + userLoginReqDto.getUsername()))){
            throw new ClientException("用户已登录");
        }
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put("login_" + userLoginReqDto.getUsername(),uuid,JSON.toJSONString(userDo));
        stringRedisTemplate.expire("login_" + userLoginReqDto.getUsername(), 30L, TimeUnit.DAYS);
        return new UserLoginRespDto(uuid);
    }
}

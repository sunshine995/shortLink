package com.nwnu.shortlink.admin.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwnu.shortlink.admin.common.convention.expection.ClientException;
import com.nwnu.shortlink.admin.common.enums.UserErrorCode;
import com.nwnu.shortlink.admin.dao.entity.UserDo;
import com.nwnu.shortlink.admin.dao.mapper.UserMapper;
import com.nwnu.shortlink.admin.dto.req.UserRegisterReqDto;
import com.nwnu.shortlink.admin.dto.resp.UserRespDTO;
import com.nwnu.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static com.nwnu.shortlink.admin.common.enums.UserErrorCode.USER_NAME_EXIST_CODE;
import static com.nwnu.shortlink.admin.common.enums.UserErrorCode.USER_SAVE_CODE;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDo> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;



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
        if (!hasUsername(requestParam.getUsername())){
            throw new ClientException(USER_NAME_EXIST_CODE);
        }
        int insert = baseMapper.insert(BeanUtil.copyProperties(requestParam, UserDo.class));
        if (insert < 1){
            throw new ClientException(USER_SAVE_CODE);
        }
        userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
    }
}

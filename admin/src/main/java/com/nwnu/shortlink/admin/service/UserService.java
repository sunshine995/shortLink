package com.nwnu.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nwnu.shortlink.admin.dao.entity.UserDo;
import com.nwnu.shortlink.admin.dto.req.UserRegisterReqDto;
import com.nwnu.shortlink.admin.dto.resp.UserRespDTO;
import org.springframework.stereotype.Service;

/**
 * 用户服务层
 */
@Service
public interface UserService extends IService<UserDo> {

    /**
     * 根据用户名返回用户
     * @param username 用户名
     * @return 返回用户实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户名是否可用
     * @param username
     * @return
     */
    Boolean hasUsername(String username);

    void register(UserRegisterReqDto requestParam);
}

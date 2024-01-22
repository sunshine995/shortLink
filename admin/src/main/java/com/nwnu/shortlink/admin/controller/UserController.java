package com.nwnu.shortlink.admin.controller;

import com.nwnu.shortlink.admin.common.convention.result.Result;
import com.nwnu.shortlink.admin.common.enums.UserErrorCode;
import com.nwnu.shortlink.admin.dto.resp.UserRespDTO;
import com.nwnu.shortlink.admin.service.UserService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    // @Autowired
    private final UserService userService;

    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username){
        UserRespDTO result = userService.getUserByUsername(username);
        if (result == null){
            return new Result<UserRespDTO>().setCode(UserErrorCode.USER_NULL_CODE.code()).setMessage(UserErrorCode.USER_NULL_CODE.message());
        }else {
            return new Result<UserRespDTO>().setCode("0").setData(userService.getUserByUsername(username));
        }
    }
}

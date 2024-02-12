package com.nwnu.shortlink.admin.controller;

import com.nwnu.shortlink.admin.common.convention.result.Result;
import com.nwnu.shortlink.admin.common.convention.result.Results;
import com.nwnu.shortlink.admin.dto.req.UserLoginReqDto;
import com.nwnu.shortlink.admin.dto.req.UserRegisterReqDto;
import com.nwnu.shortlink.admin.dto.req.UserUpdateReqDto;
import com.nwnu.shortlink.admin.dto.resp.UserLoginRespDto;
import com.nwnu.shortlink.admin.dto.resp.UserRespDTO;
import com.nwnu.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制层
 */
@Controller
@RestController
@RequiredArgsConstructor
public class UserController {

    // @Autowired
    private final UserService userService;

    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username){
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 查询用户名是否存在
     * @param username
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username){
        return Results.success(userService.hasUsername(username));
    }

    /**
     * 注册用户
     * @param userRegisterReqDto
     * @return
     */

    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> saveUser(@RequestBody UserRegisterReqDto userRegisterReqDto){
        userService.register(userRegisterReqDto);
        return Results.success();
    }

    @PutMapping("/api/short-link/admin/v1/user")
    public Result<Void> updateUser(@RequestBody UserUpdateReqDto userUpdateReqDto){
        userService.updateUser(userUpdateReqDto);
        return Results.success();
    }


    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginRespDto> login(@RequestBody UserLoginReqDto userLoginReqDto){
        UserLoginRespDto userLoginRespDto =   userService.login(userLoginReqDto);
        return Results.success(userLoginRespDto);
    }
}

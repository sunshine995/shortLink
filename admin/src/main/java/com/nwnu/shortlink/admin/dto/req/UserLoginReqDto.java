package com.nwnu.shortlink.admin.dto.req;

import lombok.Data;

@Data
public class UserLoginReqDto {
    /**
     * 用户登录的账号和密码
     */
    private String username;

    private String password;
}

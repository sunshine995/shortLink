package com.nwnu.shortlink.project.dto.resp;

import lombok.Data;

/**
 * 短链接分组返回信息
 */
@Data
public class ShortLinkGroupRespDto {

    // 分组id
    private String gid;
    // 分组名称
    private String name;
}

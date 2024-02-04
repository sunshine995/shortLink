package com.nwnu.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 短连接分组创建参数
 */
@Data
public class ShortLinkGroupSaveReqDto {

    /**
     * 分组名
     */
    private String name;
}

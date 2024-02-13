package com.nwnu.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nwnu.shortlink.project.dao.entity.ShortLinkDo;
import lombok.Data;

/**
 * 短链接分页请求参数
 */
@Data
public class ShortLinkPageReqDTO extends Page<ShortLinkDo> {
    /**
     * 分组标识
     */
    private String gid;
}

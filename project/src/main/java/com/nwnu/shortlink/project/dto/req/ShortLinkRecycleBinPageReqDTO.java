package com.nwnu.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nwnu.shortlink.project.dao.entity.ShortLinkDo;
import lombok.Data;

import java.util.List;

/**
 * 回收站短链接分页请求参数
 */
@Data
public class ShortLinkRecycleBinPageReqDTO extends Page<ShortLinkDo> {

    /**
     * 分组标识
     */
    private List<String> gidList;
}

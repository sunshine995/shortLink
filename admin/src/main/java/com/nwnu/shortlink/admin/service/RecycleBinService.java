package com.nwnu.shortlink.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nwnu.shortlink.admin.common.convention.result.Result;
import com.nwnu.shortlink.admin.remote.req.ShortLinkRecycleBinPageReqDTO;
import com.nwnu.shortlink.admin.remote.resp.ShortLinkPageRespDTO;


/**
 * URL 回收站接口层
 */
public interface RecycleBinService {

    /**
     * 分页查询回收站短链接
     *
     * @param requestParam 请求参数
     * @return 返回参数包装
     */
    Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam);
}

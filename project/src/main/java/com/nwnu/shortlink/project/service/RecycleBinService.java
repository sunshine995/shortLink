package com.nwnu.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nwnu.shortlink.project.dao.entity.ShortLinkDo;
import com.nwnu.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.nwnu.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.nwnu.shortlink.project.dto.resp.ShortLinkPageRespDTO;


/**
 * 回收站管理接口层
 */
public interface RecycleBinService extends IService<ShortLinkDo> {

    /**
     * 保存回收站
     *
     * @param requestParam 请求参数
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

    // 分页查询功能
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam);
}
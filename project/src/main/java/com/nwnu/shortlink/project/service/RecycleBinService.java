package com.nwnu.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nwnu.shortlink.project.dao.entity.ShortLinkDo;
import com.nwnu.shortlink.project.dto.req.RecycleBinSaveReqDTO;


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
}
package com.nwnu.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nwnu.shortlink.project.dao.entity.ShortLinkDo;
import com.nwnu.shortlink.project.dto.req.ShortLinkCreateReqDto;
import com.nwnu.shortlink.project.dto.resp.ShortLinkCreateRespDto;

public interface ShortLinkService extends IService<ShortLinkDo> {

    /**
     * 创建短链接
     * @param shortLinkCreateReqDto 短链接传入参数
     * @return 短链接返回参数
     */
    ShortLinkCreateRespDto createShortLink(ShortLinkCreateReqDto shortLinkCreateReqDto);
}

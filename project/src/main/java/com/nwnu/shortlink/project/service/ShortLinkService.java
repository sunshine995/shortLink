package com.nwnu.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nwnu.shortlink.project.dao.entity.ShortLinkDo;
import com.nwnu.shortlink.project.dto.req.ShortLinkCreateReqDto;
import com.nwnu.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.nwnu.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.nwnu.shortlink.project.dto.resp.ShortLinkCreateRespDto;
import com.nwnu.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nwnu.shortlink.project.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDo> {

    /**
     * 创建短链接
     * @param shortLinkCreateReqDto 短链接传入参数
     * @return 短链接返回参数
     */
    ShortLinkCreateRespDto createShortLink(ShortLinkCreateReqDto shortLinkCreateReqDto);

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页查询短链接请求参数
     * @return 短链接分页返回结果
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 查询短链接的数量
     * @param requestParam 查询短链接的请求参数
     * @return 响应
     */
    List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam);

    /**
     * 修改短链接
     *
     * @param requestParam 修改短链接请求参数
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);
}

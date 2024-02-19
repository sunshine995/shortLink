package com.nwnu.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nwnu.shortlink.admin.common.convention.result.Result;
import com.nwnu.shortlink.admin.remote.dto.ShortLinkRemote;
import com.nwnu.shortlink.admin.remote.dto.req.ShortLinkCreateReqDto;
import com.nwnu.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.nwnu.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDto;
import com.nwnu.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShortLinkController {

    /**
     * 后续重构为 SpringCloud Feign 调用
     */
    ShortLinkRemote shortLinkRemoteService = new ShortLinkRemote() {
    };

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDto> createShortLink(@RequestBody ShortLinkCreateReqDto requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return shortLinkRemoteService.pageShortLink(requestParam);
    }
}

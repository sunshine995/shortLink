package com.nwnu.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nwnu.shortlink.admin.common.convention.result.Result;
import com.nwnu.shortlink.admin.common.convention.result.Results;
import com.nwnu.shortlink.admin.dto.req.RecycleBinSaveReqDTO;
import com.nwnu.shortlink.admin.remote.ShortLinkRemote;
import com.nwnu.shortlink.admin.remote.req.RecycleBinRecoverReqDTO;
import com.nwnu.shortlink.admin.remote.req.ShortLinkRecycleBinPageReqDTO;
import com.nwnu.shortlink.admin.remote.resp.ShortLinkPageRespDTO;
import com.nwnu.shortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站管理控制层
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    /**
     * 后续重构为 SpringCloud Feign 调用
     */
    ShortLinkRemote shortLinkRemoteService = new ShortLinkRemote() {
    };

    /**
     * 保存回收站
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        shortLinkRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        return recycleBinService.pageRecycleBinShortLink(requestParam);
    }


    /**
     * 恢复短链接
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        shortLinkRemoteService.recoverRecycleBin(requestParam);
        return Results.success();
    }
}

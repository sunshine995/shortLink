package com.nwnu.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nwnu.shortlink.project.common.convention.result.Result;
import com.nwnu.shortlink.project.common.convention.result.Results;
import com.nwnu.shortlink.project.dto.req.ShortLinkCreateReqDto;
import com.nwnu.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.nwnu.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.nwnu.shortlink.project.dto.resp.ShortLinkCreateRespDto;
import com.nwnu.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nwnu.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.nwnu.shortlink.project.service.ShortLinkService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接创建
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;


    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDto> createShortLink(@RequestBody ShortLinkCreateReqDto shortLinkCreateReqDto) {
        return Results.success(shortLinkService.createShortLink(shortLinkCreateReqDto));
    }

    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }

    /**
     * 查询短链接分组内数量
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam("requestParam") List<String> requestParam) {
        return Results.success(shortLinkService.listGroupShortLinkCount(requestParam));
    }

    /**
     * 修改短链接
     * @param requestParam 参数
     * @return 返回值
     */

    @PostMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkService.updateShortLink(requestParam);
        return Results.success();
    }

    @GetMapping("/{short-uri}")
    public void restoreUrl(@PathVariable("short-uri") String shortUri, ServletRequest request, ServletResponse response) {
        shortLinkService.restoreUrl(shortUri, request, response);
    }

}

package com.nwnu.shortlink.project.controller;

import com.nwnu.shortlink.project.common.convention.result.Result;
import com.nwnu.shortlink.project.common.convention.result.Results;
import com.nwnu.shortlink.project.dto.req.ShortLinkCreateReqDto;
import com.nwnu.shortlink.project.dto.resp.ShortLinkCreateRespDto;
import com.nwnu.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}

package com.nwnu.shortlink.admin.controller;

import com.nwnu.shortlink.admin.common.convention.result.Result;
import com.nwnu.shortlink.admin.common.convention.result.Results;
import com.nwnu.shortlink.admin.dto.req.ShortLinkGroupSaveReqDto;
import com.nwnu.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短连接分组控制层
 */

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/api/short-link/v1/group")
    public Result<Void> save(@RequestBody ShortLinkGroupSaveReqDto shortLinkGroupSaveReqDto){
        groupService.saveGroup(shortLinkGroupSaveReqDto.getName());
        return Results.success();
    }
}

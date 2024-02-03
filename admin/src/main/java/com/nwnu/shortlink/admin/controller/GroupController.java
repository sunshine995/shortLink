package com.nwnu.shortlink.admin.controller;

import com.nwnu.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短连接分组控制层
 */

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
}

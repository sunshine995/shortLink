package com.nwnu.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwnu.shortlink.admin.dao.entity.GroupDo;
import com.nwnu.shortlink.admin.dao.mapper.GroupMapper;
import com.nwnu.shortlink.admin.service.GroupService;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 短连接分组接口实现层
 */

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDo> implements GroupService {

}

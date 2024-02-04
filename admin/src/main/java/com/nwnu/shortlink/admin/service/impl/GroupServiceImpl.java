package com.nwnu.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwnu.shortlink.admin.dao.entity.GroupDo;
import com.nwnu.shortlink.admin.dao.mapper.GroupMapper;
import com.nwnu.shortlink.admin.service.GroupService;
import com.nwnu.shortlink.admin.toolkit.RandomGenerator;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 短连接分组接口实现层
 */

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDo> implements GroupService {

    @Override
    public void saveGroup(String groupName) {
        String gid;
        do {
            gid = RandomGenerator.generateRandom();
        }while (!hasGid(gid));

        GroupDo build = GroupDo.builder()
                .gid(gid)
                .name(groupName)
                .build();
        baseMapper.insert(build);

    }

    private boolean hasGid(String gid){
        LambdaQueryWrapper<GroupDo> wrapper = Wrappers.lambdaQuery(GroupDo.class).
                eq(GroupDo::getGid, gid)
                // TODO 设置用户名
                .eq(GroupDo::getUsername, null)
                .eq(GroupDo::getDelFlag, 0);
        GroupDo groupDo = baseMapper.selectOne(wrapper);
        return groupDo == null;
    }
}

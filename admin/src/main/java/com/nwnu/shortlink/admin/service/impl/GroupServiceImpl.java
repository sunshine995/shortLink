package com.nwnu.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwnu.shortlink.admin.common.biz.user.UserContext;
import com.nwnu.shortlink.admin.dao.entity.GroupDo;
import com.nwnu.shortlink.admin.dao.mapper.GroupMapper;
import com.nwnu.shortlink.admin.dto.resp.ShortLinkGroupRespDto;
import com.nwnu.shortlink.admin.service.GroupService;
import com.nwnu.shortlink.admin.toolkit.RandomGenerator;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<ShortLinkGroupRespDto> listGroup() {
        // TODO 获取用户名
        System.out.println(UserContext.getUsername());
        LambdaQueryWrapper<GroupDo> groupDoLambdaQueryWrapper = Wrappers.lambdaQuery(GroupDo.class)
                .eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(GroupDo::getDelFlag, 0)
                .orderByDesc(GroupDo::getSortOrder, GroupDo::getUpdateTime);
        List<GroupDo> groupDos = baseMapper.selectList(groupDoLambdaQueryWrapper);

        return BeanUtil.copyToList(groupDos, ShortLinkGroupRespDto.class);
    }

    private boolean hasGid(String gid){
        LambdaQueryWrapper<GroupDo> wrapper = Wrappers.lambdaQuery(GroupDo.class).
                eq(GroupDo::getGid, gid)
                // TODO 设置用户名
                .eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(GroupDo::getDelFlag, 0);
        GroupDo groupDo = baseMapper.selectOne(wrapper);
        return groupDo == null;
    }
}

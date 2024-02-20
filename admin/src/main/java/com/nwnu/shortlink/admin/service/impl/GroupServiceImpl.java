package com.nwnu.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwnu.shortlink.admin.common.biz.user.UserContext;
import com.nwnu.shortlink.admin.common.convention.result.Result;
import com.nwnu.shortlink.admin.dao.entity.GroupDo;
import com.nwnu.shortlink.admin.dao.mapper.GroupMapper;
import com.nwnu.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDto;
import com.nwnu.shortlink.admin.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nwnu.shortlink.admin.dto.resp.ShortLinkGroupRespDto;
import com.nwnu.shortlink.admin.remote.ShortLinkRemote;
import com.nwnu.shortlink.admin.service.GroupService;
import com.nwnu.shortlink.admin.toolkit.RandomGenerator;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 短连接分组接口实现层
 */

@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDo> implements GroupService {

    ShortLinkRemote shortLinkRemoteService = new ShortLinkRemote() {
    };

    @Override
    public void saveGroup(String groupName) {
        String gid;
        do {
            gid = RandomGenerator.generateRandom();
        }while (!hasGid(gid));


        GroupDo build = GroupDo.builder()
                .gid(gid)
                .sortOrder(0)
                .username("mall")
                .name(groupName)
                .build();
        baseMapper.insert(build);

    }

    @Override
    public List<ShortLinkGroupRespDto> listGroup() {
        // TODO 获取用户名
        System.out.println(UserContext.getUsername());
        LambdaQueryWrapper<GroupDo> groupDoLambdaQueryWrapper = Wrappers.lambdaQuery(GroupDo.class)
                .eq(GroupDo::getUsername, "mall")
                .eq(GroupDo::getDelFlag, 0)
                .orderByDesc(GroupDo::getSortOrder, GroupDo::getUpdateTime);
        List<GroupDo> groupDos = baseMapper.selectList(groupDoLambdaQueryWrapper);

        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = shortLinkRemoteService
                .listGroupShortLinkCount(groupDos.stream().map(GroupDo::getGid).toList());
        List<ShortLinkGroupRespDto> shortLinkGroupRespDTOList = BeanUtil.copyToList(groupDos, ShortLinkGroupRespDto.class);
        shortLinkGroupRespDTOList.forEach(each -> {
            Optional<ShortLinkGroupCountQueryRespDTO> first = listResult.getData().stream()
                    .filter(item -> Objects.equals(item.getGid(), each.getGid()))
                    .findFirst();
            first.ifPresent(item -> each.setShortLinkCount(first.get().getShortLinkCount()));
        });
        return shortLinkGroupRespDTOList;
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDto shortLinkGroupUpdateReqDto) {
        LambdaUpdateWrapper<GroupDo> wrapper = Wrappers.lambdaUpdate(GroupDo.class)
                .eq(GroupDo::getGid, shortLinkGroupUpdateReqDto.getId())
                .eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(GroupDo::getDelFlag, 0);
        GroupDo groupDo = new GroupDo();
        groupDo.setName(shortLinkGroupUpdateReqDto.getName());
        baseMapper.update(groupDo, wrapper);

    }

    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper<GroupDo> wrapper = Wrappers.lambdaUpdate(GroupDo.class)
                .eq(GroupDo::getGid, gid)
                .eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(GroupDo::getDelFlag, 0);
        GroupDo groupDo = new GroupDo();
        groupDo.setDelFlag(1);
        baseMapper.update(groupDo, wrapper);
    }



    private boolean hasGid(String gid){
        LambdaQueryWrapper<GroupDo> wrapper = Wrappers.lambdaQuery(GroupDo.class).
                eq(GroupDo::getGid, gid)
                // TODO 设置用户名
                .eq(GroupDo::getUsername, "mall");
        System.out.println(UserContext.getUsername());
        GroupDo groupDo = baseMapper.selectOne(wrapper);
        return groupDo == null;
    }
}

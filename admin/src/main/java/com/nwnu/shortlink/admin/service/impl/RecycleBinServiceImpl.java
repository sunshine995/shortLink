package com.nwnu.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.nwnu.shortlink.admin.common.biz.user.UserContext;
import com.nwnu.shortlink.admin.common.convention.expection.ServiceException;
import com.nwnu.shortlink.admin.common.convention.result.Result;
import com.nwnu.shortlink.admin.dao.entity.GroupDo;
import com.nwnu.shortlink.admin.dao.mapper.GroupMapper;
import com.nwnu.shortlink.admin.remote.ShortLinkRemote;
import com.nwnu.shortlink.admin.remote.req.ShortLinkRecycleBinPageReqDTO;
import com.nwnu.shortlink.admin.remote.resp.ShortLinkPageRespDTO;
import com.nwnu.shortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * URL 回收站接口实现层
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final GroupMapper groupMapper;

    /**
     * 后续重构为 SpringCloud Feign 调用
     */
    ShortLinkRemote shortLinkRemoteService = new ShortLinkRemote() {
    };

    @Override
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<GroupDo> queryWrapper = Wrappers.lambdaQuery(GroupDo.class)
                .eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(GroupDo::getDelFlag, 0);
        List<GroupDo> groupDOList = groupMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupDOList)) {
            throw new ServiceException("用户无分组信息");
        }
        requestParam.setGidList(groupDOList.stream().map(GroupDo::getGid).toList());
        return shortLinkRemoteService.pageRecycleBinShortLink(requestParam);
    }
}
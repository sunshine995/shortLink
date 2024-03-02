package com.nwnu.shortlink.project.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwnu.shortlink.project.dao.entity.ShortLinkDo;
import com.nwnu.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nwnu.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.nwnu.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.nwnu.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.nwnu.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.nwnu.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.nwnu.shortlink.project.common.constant.RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY;
import static com.nwnu.shortlink.project.common.constant.RedisKeyConstant.GOTO_SHORT_LINK_KEY;


/**
 * 回收站管理接口实现层
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDo> implements RecycleBinService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDo> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDo.class)
                .eq(ShortLinkDo::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDo::getGid, requestParam.getGid())
                .eq(ShortLinkDo::getEnableStatus, 0)
                .eq(ShortLinkDo::getDelFlag, 0);
        ShortLinkDo shortLinkDO = ShortLinkDo.builder()
                .enableStatus(1)
                .build();
        baseMapper.update(shortLinkDO, updateWrapper);
        stringRedisTemplate.delete(String.format(GOTO_SHORT_LINK_KEY, requestParam.getFullShortUrl()));
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDo> queryWrapper = Wrappers.lambdaQuery(ShortLinkDo.class)
                .in(ShortLinkDo::getGid, requestParam.getGidList())
                .eq(ShortLinkDo::getEnableStatus, 1)
                .eq(ShortLinkDo::getDelFlag, 0)
                .orderByDesc(ShortLinkDo::getUpdateTime);
        IPage<ShortLinkDo> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resultPage.convert(each -> {
            ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            result.setDomain("http://" + result.getDomain());
            return result;
        });
     }

    @Override
    public void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDo> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDo.class)
                .eq(ShortLinkDo::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDo::getGid, requestParam.getGid())
                .eq(ShortLinkDo::getEnableStatus, 1)
                .eq(ShortLinkDo::getDelFlag, 0);
        ShortLinkDo shortLinkDO = ShortLinkDo.builder()
                .enableStatus(0)
                .build();
        baseMapper.update(shortLinkDO, updateWrapper);
        stringRedisTemplate.delete(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, requestParam.getFullShortUrl()));
    }

}
package com.nwnu.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwnu.shortlink.project.common.convention.expection.ServiceException;
import com.nwnu.shortlink.project.config.RBloomFilterConfiguration;
import com.nwnu.shortlink.project.dao.entity.ShortLinkDo;
import com.nwnu.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nwnu.shortlink.project.dto.req.ShortLinkCreateReqDto;
import com.nwnu.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.nwnu.shortlink.project.dto.resp.ShortLinkCreateRespDto;
import com.nwnu.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.nwnu.shortlink.project.service.ShortLinkService;
import com.nwnu.shortlink.project.toolkit.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDo> implements ShortLinkService {

    private final RBloomFilter<String> shortLinkCreatCachePenetrationBloomFilter;

    @Override
    public ShortLinkCreateRespDto createShortLink(ShortLinkCreateReqDto shortLinkCreateReqDto) {
        String shortUrl = generateSuffix(shortLinkCreateReqDto);
        String fullUrl = shortLinkCreateReqDto.getDomain() + "/" + shortUrl;
        ShortLinkDo shortLinkDo = BeanUtil.copyProperties(shortLinkCreateReqDto, ShortLinkDo.class);
        shortLinkDo.setShortUrl(shortUrl);
        System.out.println(shortLinkDo.getDescribe());
        shortLinkDo.setFullShortUrl(shortLinkCreateReqDto.getDomain() + "/" + shortUrl);
        // 如果重复添加抛出异常
        try {
            baseMapper.insert(shortLinkDo);
        }catch (DuplicateKeyException ex){
            // TODO 已经误判的短链接如何处理，
            // 真实存在在数据库中
            // 不一定存在在缓存中
            log.warn("短链接：{} 重复入库", fullUrl);
            throw new ServiceException("短链接生成重复");
        }
        // 将短链接的值插入布隆过滤器
        shortLinkCreatCachePenetrationBloomFilter.add(shortUrl);
        return ShortLinkCreateRespDto.builder()
                .fullShortUrl(shortLinkDo.getFullShortUrl())
                .gid(shortLinkDo.getGid())
                .origin(shortLinkDo.getOrigin())
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDo> wrapper = Wrappers.lambdaQuery(ShortLinkDo.class)
                .eq(ShortLinkDo::getGid, requestParam.getGid())
                .eq(ShortLinkDo::getEnableStatus, 0)
                .eq(ShortLinkDo::getDelFlag, 0)
                .orderByDesc(ShortLinkDo::getUpdateTime);
        IPage<ShortLinkDo> pageReq = baseMapper.selectPage(requestParam, wrapper);
        return pageReq.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

    // 通过原始链接生成一个对应参数
    private String generateSuffix(ShortLinkCreateReqDto shortLinkCreateReqDto){
        int customGenerateCount = 0;
        String shortUri;

        while (true){
            if (customGenerateCount > 10){
                throw new ServiceException("短链接频繁创建，请稍后重试");
            }
            String origin = shortLinkCreateReqDto.getOrigin();
            origin += System.currentTimeMillis();
            shortUri = HashUtil.hashToBase62(origin);
            if (!shortLinkCreatCachePenetrationBloomFilter.contains(shortLinkCreateReqDto.getDomain() + "/" + shortUri)){
                break;
            }
            customGenerateCount++;
        }
        return shortUri;
    }
}

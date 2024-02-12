package com.nwnu.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwnu.shortlink.project.dao.entity.ShortLinkDo;
import com.nwnu.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nwnu.shortlink.project.dto.req.ShortLinkCreateReqDto;
import com.nwnu.shortlink.project.dto.resp.ShortLinkCreateRespDto;
import com.nwnu.shortlink.project.service.ShortLinkService;
import com.nwnu.shortlink.project.toolkit.HashUtil;
import org.springframework.stereotype.Service;

@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDo> implements ShortLinkService {

    @Override
    public ShortLinkCreateRespDto createShortLink(ShortLinkCreateReqDto shortLinkCreateReqDto) {
        String shortUrl = generateSuffix(shortLinkCreateReqDto);
        ShortLinkDo shortLinkDo = BeanUtil.copyProperties(shortLinkCreateReqDto, ShortLinkDo.class);
        shortLinkDo.setShortUrl(shortUrl);
        System.out.println(shortLinkDo.getDescribe());
        shortLinkDo.setFullShortUrl(shortLinkCreateReqDto.getDomain() + "/" + shortUrl);
        baseMapper.insert(shortLinkDo);

        return ShortLinkCreateRespDto.builder()
                .fullShortUrl(shortLinkDo.getFullShortUrl())
                .gid(shortLinkDo.getGid())
                .origin(shortLinkDo.getOrigin())
                .build();
    }

    // 通过原始链接生成一个对应参数
    private String generateSuffix(ShortLinkCreateReqDto shortLinkCreateReqDto){
        String origin = shortLinkCreateReqDto.getOrigin();
        return HashUtil.hashToBase62(origin);
    }
}

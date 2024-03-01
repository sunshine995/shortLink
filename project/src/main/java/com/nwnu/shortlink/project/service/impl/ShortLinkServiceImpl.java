package com.nwnu.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwnu.shortlink.project.common.convention.expection.ClientException;
import com.nwnu.shortlink.project.common.convention.expection.ServiceException;
import com.nwnu.shortlink.project.common.enums.VailDateTypeEnum;
import com.nwnu.shortlink.project.dao.entity.ShortLinkDo;
import com.nwnu.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.nwnu.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.nwnu.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nwnu.shortlink.project.dto.req.ShortLinkCreateReqDto;
import com.nwnu.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.nwnu.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.nwnu.shortlink.project.dto.resp.ShortLinkCreateRespDto;
import com.nwnu.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nwnu.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.nwnu.shortlink.project.service.ShortLinkService;
import com.nwnu.shortlink.project.toolkit.HashUtil;
import com.nwnu.shortlink.project.toolkit.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.nwnu.shortlink.project.common.constant.RedisKeyConstant.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDo> implements ShortLinkService {

    private final RBloomFilter<String> shortLinkCreatCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    @Override
    public ShortLinkCreateRespDto createShortLink(ShortLinkCreateReqDto shortLinkCreateReqDto) {
        String shortUrl = generateSuffix(shortLinkCreateReqDto);
        String fullUrl = shortLinkCreateReqDto.getDomain() + "/" + shortUrl;
        ShortLinkDo shortLinkDo = BeanUtil.copyProperties(shortLinkCreateReqDto, ShortLinkDo.class);
        shortLinkDo.setShortUrl(shortUrl);
        System.out.println(shortLinkDo.getDescribe());
        shortLinkDo.setFullShortUrl(shortLinkCreateReqDto.getDomain() + "/" + shortUrl);

        ShortLinkGotoDO linkGotoDO = ShortLinkGotoDO.builder()
                .fullShortUrl(fullUrl)
                .gid(shortLinkCreateReqDto.getGid())
                .build();
        // 如果重复添加抛出异常
        try {
            baseMapper.insert(shortLinkDo);
            shortLinkGotoMapper.insert(linkGotoDO);
        }catch (DuplicateKeyException ex){
            // TODO 已经误判的短链接如何处理，
            // 真实存在在数据库中
            // 不一定存在在缓存中
            log.warn("短链接：{} 重复入库", fullUrl);
            throw new ServiceException("短链接生成重复");
        }
        stringRedisTemplate.opsForValue().set(
                String.format(GOTO_SHORT_LINK_KEY, fullUrl),
                shortLinkCreateReqDto.getOrigin(),
                LinkUtil.getLinkCacheValidTime(shortLinkCreateReqDto.getValidDate()), TimeUnit.MILLISECONDS
        );
        // 将短链接的值插入布隆过滤器
        shortLinkCreatCachePenetrationBloomFilter.add(fullUrl);
        return ShortLinkCreateRespDto.builder()
                .fullShortUrl("http://" + shortLinkDo.getFullShortUrl())
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
        return pageReq.convert(each -> {
            ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            result.setDomain("http://" + result.getDomain());
            return result;
        });
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDo> queryWrapper = Wrappers.query(new ShortLinkDo())
                .select("gid as gid, count(*) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> shortLinkDOList = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(shortLinkDOList, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDo> queryWrapper = Wrappers.lambdaQuery(ShortLinkDo.class)
                .eq(ShortLinkDo::getGid, requestParam.getGid())
                .eq(ShortLinkDo::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDo::getDelFlag, 0)
                .eq(ShortLinkDo::getEnableStatus, 0);
        ShortLinkDo hasShortLinkDO = baseMapper.selectOne(queryWrapper);
        if (hasShortLinkDO == null) {
            throw new ClientException("短链接记录不存在");
        }
        ShortLinkDo shortLinkDO = ShortLinkDo.builder()
                .domain(hasShortLinkDO.getDomain())
                .shortUrl(hasShortLinkDO.getShortUrl())
                .clickNum(hasShortLinkDO.getClickNum())
                .favicon(hasShortLinkDO.getFavicon())
                .createType(hasShortLinkDO.getCreateType())
                .gid(requestParam.getGid())
                .origin(requestParam.getOriginUrl())
                .describe(requestParam.getDescribe())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .build();

        if (Objects.equals(hasShortLinkDO.getGid(), requestParam.getGid())) {
            LambdaUpdateWrapper<ShortLinkDo> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDo.class)
                    .eq(ShortLinkDo::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDo::getGid, requestParam.getGid())
                    .eq(ShortLinkDo::getDelFlag, 0)
                    .eq(ShortLinkDo::getEnableStatus, 0)
                    .set(Objects.equals(requestParam.getValidDateType(), VailDateTypeEnum.PERMANENT.getType()), ShortLinkDo::getValidDate, null);
            baseMapper.update(shortLinkDO, updateWrapper);
        } else {
            LambdaUpdateWrapper<ShortLinkDo> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDo.class)
                    .eq(ShortLinkDo::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDo::getGid, hasShortLinkDO.getGid())
                    .eq(ShortLinkDo::getDelFlag, 0)
                    .eq(ShortLinkDo::getEnableStatus, 0);
            baseMapper.delete(updateWrapper);
            baseMapper.insert(shortLinkDO);
        }

    }

    @SneakyThrows
    @Override
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) {
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;
        // 首先查询缓存中是否存在，存在的话直接跳转
        String originalLink   = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
        // 如果短链接不为空这直接跳转，从缓存中拿数据
        if (StrUtil.isNotBlank(originalLink)) {
            ((HttpServletResponse) response).sendRedirect(originalLink);
            return;
        }
        // 查看布隆过滤器中是否存在，如果存在，则查询数据库，不存在直接返回空
        boolean contains = shortLinkCreatCachePenetrationBloomFilter.contains(fullShortUrl);
        if (!contains) {
            return;
        }
        // 查看缓存中是否为空，为空直接返回
        String gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(gotoIsNullShortLink)) {
            return;
        }
        // 如果不为空使用Redis中的锁机制看门狗机制
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            // 使用双重锁判别机制
            // 首先对短链接加锁，然后再次检查缓存，如果这时候缓存中存在原始链接，则直接重定向，这可以处理高并发下的缓存击穿问题
            originalLink   = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
            // 如果短链接不为空这直接跳转，从缓存中拿数据
            if (StrUtil.isNotBlank(originalLink)) {
                ((HttpServletResponse) response).sendRedirect(originalLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
            if (shortLinkGotoDO == null) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.MINUTES);
                // 严谨来说此处需要进行封控
                return;
            }
            System.out.println(shortLinkGotoDO.getGid() + "0000000000000");
            LambdaQueryWrapper<ShortLinkDo> queryWrapper = Wrappers.lambdaQuery(ShortLinkDo.class)
                    .eq(ShortLinkDo::getGid, shortLinkGotoDO.getGid())
                    .eq(ShortLinkDo::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDo::getDelFlag, 0)
                    .eq(ShortLinkDo::getEnableStatus, 0);
            ShortLinkDo shortLinkDO = baseMapper.selectOne(queryWrapper);
            if (shortLinkDO != null) {
                // 将查询到的原始链接放入到Redis中
                if (shortLinkDO.getValidDate() != null && shortLinkDO.getValidDate().before(new Date())) {
                    stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.MINUTES);
                    return;
                }
                stringRedisTemplate.opsForValue().set(
                        String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),
                        shortLinkDO.getOrigin(),
                        LinkUtil.getLinkCacheValidTime(shortLinkDO.getValidDate()), TimeUnit.MILLISECONDS
                );
                ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOrigin());
            }
        }finally {
            lock.unlock();
        }

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

package com.nwnu.shortlink.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nwnu.shortlink.admin.dao.entity.GroupDo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短连接分组 mapper
 */

@Mapper
public interface GroupMapper extends BaseMapper<GroupDo> {
}

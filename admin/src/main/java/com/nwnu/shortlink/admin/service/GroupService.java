package com.nwnu.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nwnu.shortlink.admin.dao.entity.GroupDo;
import com.nwnu.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDto;
import com.nwnu.shortlink.admin.dto.resp.ShortLinkGroupRespDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 短连接分组 接口层
 */
@Service
public interface GroupService extends IService<GroupDo> {

    /**
     * 新增短连接分组
     * @param groupName 短连接分组名
     */

    void saveGroup(String groupName);

    List<ShortLinkGroupRespDto> listGroup();

    /**
     * 更新分组
     * @param shortLinkGroupUpdateReqDto
     */
    void updateGroup(ShortLinkGroupUpdateReqDto shortLinkGroupUpdateReqDto);


    /**
     * 删除分组
     * @param gid 分组id
     */
    void deleteGroup(String gid);
}

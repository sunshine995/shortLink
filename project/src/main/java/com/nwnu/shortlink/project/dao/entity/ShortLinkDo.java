package com.nwnu.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_link")
public class ShortLinkDo {
    /**
     * id
     */
    private Long id;

    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUrl;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String origin;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 启用标识0：启用，1未启用
     */
    private int enableStatus;

    /**
     * 创建类型0：控制台，1接口创建
     */
    private int createType;

    // 分组表示
    private String gid;

    /**
     * 有效类型0：永久有效，1自定义
     */
    private int validDateType;

    /**
     * 有效期
     */
    private Date validDate;

    /**
     * 描述
     */
    @TableField("`describe`")
    private String describe;

}

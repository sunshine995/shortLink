package com.nwnu.shortlink.admin.remote.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkPageRespDTO {

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



    // 分组表示
    private String gid;

    /**
     * 有效类型0：永久有效，1自定义
     */
    private int validDateType;

    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-8")
    private Date validDate;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-8")
    private Date creatTime;

    /**
     * 描述
     */
    private String describe;


    /**
     * 网站标识
     */
    private String favicon;



    /**
     * 历史PV
     */
    private Integer totalPv;

    /**
     * 今日PV
     */
    private Integer toDayPv;

    /**
     * 历史UV
     */
    private Integer totalUv;

    /**
     * 今日UV
     */
    private Integer toDayUv;

    /**
     * 历史UIP
     */
    private Integer totalUIp;

    /**
     * 今日UIP
     */
    private Integer toDayUIp;
}

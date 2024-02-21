package com.nwnu.shortlink.project.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 短链接创建对象
 */
@Data
public class ShortLinkCreateReqDto {

    /**
     * 域名
     */
    private String domain;


    /**
     * 原始链接
     */
    private String origin;

    private String gid;

    /**
     * 创建类型0：控制台，1接口创建
     */
    private Integer createType;

    /**
     * 有效类型0：永久有效，1自定义
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    /**
     * 描述
     */
    private String describe;

}

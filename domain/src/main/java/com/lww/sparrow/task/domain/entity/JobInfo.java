package com.lww.sparrow.task.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class JobInfo implements Serializable {
    private static final long serialVersionUID = 9170165439700178728L;
    // ip列表分隔符，分号
    private static final String IP_SEPARATOR_SEMICOLON = ";";
    // ip列表分隔符，逗号
    private static final String IP_SEPARATOR_COMMA = ",";

    private Long id;
    private String jobName;
    private String timeExpression;
    private String submitIp;
    private Date executeDate;
    private Date createDate;
    private Date updateDate;
    private String executeUrl;
    private Integer isValid;
    private Integer retryCount;
}

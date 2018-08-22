package com.lww.sparrow.task.domain.entity;

import com.alibaba.fastjson.annotation.JSONField;
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
    private String businessName;
    private String name;
    private String jobOwner;
    @JSONField(serialize=false)
    private String department = "";

    private String timeExpression;
    private int jobType ;

    @JSONField(serialize=false)
    private Integer runningModel;
    @JSONField(serialize=false)
    private String executorIps = "";
    @JSONField(serialize=false)
    private Integer executorNum = 1;
    @JSONField(serialize=false)
    private String appShell = "";
    @JSONField(serialize=false)
    private String shellExeUser = "";
    @JSONField(serialize=false)
    private String requestURL = "";
    @JSONField(serialize=false)
    private Integer needResult;

    private Integer isValid;
    @JSONField(serialize=false)
    private boolean alarm = true;
    private boolean reentrant = false;
    @JSONField(serialize=false)
    private String alarmUsers = "";
    private Date executeDate;
    private Integer retryCount;

    /**
     * 任务描述，add by jyc
     */
    private String desc;

    @JSONField(jsonDirect = true)
    private String extConfig;

    private Date createDate;
    private Date modifyDate;

}

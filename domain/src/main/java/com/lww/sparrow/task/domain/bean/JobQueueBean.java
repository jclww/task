package com.lww.sparrow.task.domain.bean;

import lombok.Data;

import java.util.Date;

@Data
public class JobQueueBean {
    private Long id;
    private String jobName;
    private String timeExpression;
    /**
     * cron 表达式下次时间
     */
    private Date executeDate;
    /**
     * 扫描出来 时间
     */
    private Date scanDate;
    /**
     * 添加到队列 时间
     */
    private Date addqueueDate;

    private String executeUrl;
    private Integer retryCount;

}

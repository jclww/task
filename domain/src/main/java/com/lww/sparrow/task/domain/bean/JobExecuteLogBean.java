package com.lww.sparrow.task.domain.bean;

import lombok.Data;

import java.util.Date;

@Data
public class JobExecuteLogBean {
    /**
     * JobInfo->id
     */
    private Long jobId;

    /**
     * cron 表达式
     */
    private String timeExpression;

    /**
     * 扫描出来 时间
     */
    private Date scanDate;
    /**
     * 添加到队列 时间
     */
    private Date addqueueDate;
    /**
     * 被执行期丢到分发线程池时间 注意与JobInfo 中 executeDate区分
     */
    private Date sendDate;
    /**
     * 被执行期丢到分发线程池时间 注意与JobInfo 中 executeDate区分
     */
    private Date executeDate;

    /**
     * cron 表达式代表时间 对应 JobInfo——>executeDate
     */
    private Date expressionDate;
    /**
     * 请求url
     */
    private String executeUrl;
    /**
     * 返回结果
     */
    private String response;
    /**
     * 重试次数
     */
    private Integer retryCount;/**
     * 重试次数
     */
    private Integer executorCount;

}

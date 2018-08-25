package com.lww.sparrow.task.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JobExecuteLog {
    /**
     * JobInfo->id
     */
    private Long jobId;

    /**
     * 重试次数
     */
    private Integer executorCount;

    /**
     * 被执行期丢到分发线程池时间 注意与JobInfo 中 executeDate区分
     */
    private Date executeDate;

    /**
     * cron 表达式代表时间 对应 JobInfo——>executeDate
     */
    private Date expressionDate;

    /**
     * 执行状态:1执行成功，2执行失败
     */
    private Integer state;
    /**
     * 返回结果
     */
    private String response;
    /**
     * 扫描出来 时间
     */
    private Date scanDate;
    /**
     * 被执行期丢到分发线程池时间 注意与JobInfo 中 executeDate区分
     */
    private Date sendDate;
    /**
     * 添加到队列 时间
     */
    private Date addqueueDate;

    /**
     * 请求url
     */
    private String executeUrl;

}

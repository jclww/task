package com.lww.sparrow.task.manager.handler;

import com.alibaba.fastjson.JSON;
import com.lww.sparrow.task.dal.JobInfoMapper;
import com.lww.sparrow.task.dal.JobLogMapper;
import com.lww.sparrow.task.domain.bean.JobExecuteLogBean;
import com.lww.sparrow.task.domain.entity.JobExecuteLog;
import com.lww.sparrow.task.domain.util.OrikaBeanUtil;
import com.lww.sparrow.task.manager.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronSequenceGenerator;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpJobHandler extends JobHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(HttpJobHandler.class);

    private JobExecuteLogBean jobInfo;
    @Resource
    private OrikaBeanUtil orikaBeanUtil;
    @Resource
    private JobInfoMapper jobInfoMapper;
    @Resource
    private JobLogMapper jobLogMapper;

    public HttpJobHandler(JobExecuteLogBean jobInfo) {
        this.jobInfo = jobInfo;
    }

    @Override
    public void run() {
        boolean sendSuccess;
        try {
            sendSuccess = handler(jobInfo, 0);
            changeJobInfoAndLog(jobInfo, sendSuccess);
            logger.info("HttpJobHandler: job:{} sendSuccess:{}", JSON.toJSONString(jobInfo), sendSuccess);
        } catch (Exception e) {
            logger.error("HttpJobHandler handler job:{} exception:", JSON.toJSONString(jobInfo), e);
        }
    }

    private static Map<String, String> headers = new HashMap<>();
    static {
        headers.put("from","task");
    }

    @Override
    boolean handler(JobExecuteLogBean job, int retry){
        job.setExecutorCount(retry);
        job.setSendDate(new Date());
        HttpClientUtil.Response response = null;
        try {
            response = HttpClientUtil.request(job.getExecuteUrl(), headers);
        } catch (Exception e) {
            logger.error("job:{} send fail exception:", JSON.toJSONString(job), e);
        }
        if (response == null || HttpClientUtil.OK != response.getStatus()) {
            if (retry < job.getRetryCount())  {
                return handler(job, retry++);
            } else {
                logger.info("job:{} retry:{} still fail", retry);
                return false;
            }
        }
        job.setResponse(response.getBody());
        return true;
    }

    @Override
    boolean changeJobInfoAndLog(JobExecuteLogBean job, boolean sendSuccess) {
        JobExecuteLog jobExecuteLog = orikaBeanUtil.convert(job, JobExecuteLog.class);
        jobExecuteLog.setState(sendSuccess ? 1 : 2);
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(job.getTimeExpression());
        Date nextTriggerTime = cronSequenceGenerator.next(job.getExpressionDate());
        jobInfoMapper.updateExecuteDateById(job.getJobId(), nextTriggerTime);
        jobLogMapper.insert(jobExecuteLog);
        return true;
    }

    public static void main(String[] args) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator("0 */1 * * * *");

        Date nextTriggerTime = cronSequenceGenerator.next(new Date(1848672000000L));
        System.out.println(nextTriggerTime.getTime());
        nextTriggerTime = cronSequenceGenerator.next(new Date(nextTriggerTime.getTime()));
        System.out.println(nextTriggerTime.getTime());
        nextTriggerTime = cronSequenceGenerator.next(new Date(nextTriggerTime.getTime()));
        System.out.println(nextTriggerTime.getTime());





    }
}

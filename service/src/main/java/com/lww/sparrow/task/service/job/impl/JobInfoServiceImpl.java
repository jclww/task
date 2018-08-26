package com.lww.sparrow.task.service.job.impl;

import com.lww.sparrow.task.dal.JobInfoMapper;
import com.lww.sparrow.task.domain.entity.JobInfo;
import com.lww.sparrow.task.service.job.JobInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class JobInfoServiceImpl implements JobInfoService {

    @Resource
    private JobInfoMapper jobInfoMapper;
    @Override
    public void insert(JobInfo jobInfo) {
        jobInfoMapper.insert(jobInfo);
    }

    @Override
    public JobInfo findById(long id) {
        JobInfo ret = jobInfoMapper.getById(id);
        if (ret == null) {
            return null;
        }
        return ret;
    }
    @Override
    public void addJob(JobInfo jobInfo) {
        jobInfoMapper.insert(jobInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<JobInfo> scanJobAndUpdateNextTime(Date startDate, Date endDate, int batchSize) {
        List<JobInfo> jobList = jobInfoMapper.scanJob(startDate, endDate, batchSize);
        if (CollectionUtils.isEmpty(jobList)) {
            return new ArrayList<>();
        }
        jobList.forEach(job -> updateNextExecTime(job, endDate));
        return jobList;
    }

    /**
     * 更新下次执行时间
     * @param job 需要修改下次执行时间的job 需要保证有 id 和 cron 表达式正确
     * @param endDate 从什么时间开始 计算下次执行时间
     */
    @Override
    public Long updateNextExecTime(JobInfo job, Date endDate) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(job.getTimeExpression());
        Date nextTriggerTime = cronSequenceGenerator.next(endDate);
        return jobInfoMapper.updateExecuteDateById(job.getId(), nextTriggerTime);
    }
}

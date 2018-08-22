package com.lww.sparrow.task.service.job.impl;

import com.lww.sparrow.task.dal.JobInfoMapper;
import com.lww.sparrow.task.domain.entity.JobInfo;
import com.lww.sparrow.task.service.job.JobInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
}

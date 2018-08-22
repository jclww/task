package com.lww.sparrow.task.service.job;

import com.lww.sparrow.task.domain.entity.JobInfo;

public interface JobInfoService {
    void insert(JobInfo jobInfo);

    JobInfo findById(long id);

    void addJob(JobInfo jobInfo);
}

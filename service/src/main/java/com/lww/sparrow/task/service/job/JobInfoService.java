package com.lww.sparrow.task.service.job;

import com.lww.sparrow.task.domain.entity.JobInfo;

import java.util.Date;
import java.util.List;

public interface JobInfoService {
    void insert(JobInfo jobInfo);

    JobInfo findById(long id);

    void addJob(JobInfo jobInfo);

    List<JobInfo> scanJobAndUpdateNextTime(Date startDate, Date endDate, int batchSize);

    Long updateNextExecTime(JobInfo job, Date endDate);
}

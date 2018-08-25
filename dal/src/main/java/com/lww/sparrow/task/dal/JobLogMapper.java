package com.lww.sparrow.task.dal;

import com.lww.sparrow.task.domain.entity.JobExecuteLog;

public interface JobLogMapper {
    long insert(JobExecuteLog jobLog);

}

package com.lww.sparrow.task.manager.handler;

import com.lww.sparrow.task.domain.bean.JobExecuteLogBean;

public abstract class  JobHandler {
    abstract boolean handler(JobExecuteLogBean job,  int retry);
    abstract boolean changeJobInfoAndLog(JobExecuteLogBean job, boolean sendSuccess);
}

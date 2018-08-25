package com.lww.sparrow.task.dal;

import com.lww.sparrow.task.domain.entity.JobInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface JobInfoMapper {

    long insert(JobInfo jobInfo);

    long updateExecuteDateById(@Param("id") long id, @Param("executeDate") Date executeDate);

    JobInfo getById(@Param("id") long id);

    List<JobInfo> findByApp(@Param("app") String app);

    List<JobInfo> findAll();

    List<JobInfo> queryByUntilDateAndBatch(@Param("untilDate") Date executeDate, @Param("limit") int batchSize);

    List<JobInfo> scanJob(@Param("startDate") Date startDate, @Param("nowDate") Date nowDate, @Param("limit") int batchSize);

    Date findNextExecuteTime(Date date);

    void updateById(JobInfo jobInfo);

    void updateIsValidById(@Param("id") long id, @Param("isValid") int isValid);

    List<JobInfo> queryUnFinishJobInfo();
}

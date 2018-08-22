package com.lww.sparrow.task.dal;

import com.lww.sparrow.task.domain.entity.JobInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface JobInfoMapper {

    long insert(JobInfo jobInfo);

    long updateDateAndRetryById(@Param("id") long id, @Param("executeDate") Date executeDate, @Param("retryCount") int retryCount);

    JobInfo getById(@Param("id") long id);

    List<JobInfo> findByApp(@Param("app") String app);

    List<JobInfo> findAll();

    List<JobInfo> findByOwner(String username);

    List<JobInfo> queryByUntilDateAndBatch(@Param("untilDate") Date executeDate, @Param("limit") int batchSize);

    Date queryRecentlyExecuteDate(@Param("startDate") Date startDate);

    List<JobInfo> queryJobInfoForWeb(Map<String, Object> map);

    List<JobInfo> queryJobInfoByExecutorIp(String executorIp);

    void updateAllColumnById(JobInfo jobInfo);

    void updateById(JobInfo jobInfo);

    void updateIsValidById(@Param("id") long id, @Param("isValid") int isValid);

    int updateOwnerById(@Param("id") long id, @Param("jobOwner") String jobOwner);

    int updateDeptById(@Param("id") long id, @Param("department") String department);

    int updateOwnerAndDeptById(@Param("id") long id, @Param("jobOwner") String jobOwner, @Param("department") String department);

    JobInfo queryJobInfoByName(JobInfo jobInfo);

    void deleteJobInfoByName(JobInfo jobInfo);

    void deleteById(long id);

    List<JobInfo> queryUnFinishJobInfo();
}

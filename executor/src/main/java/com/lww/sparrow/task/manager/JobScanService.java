package com.lww.sparrow.task.manager;

import com.alibaba.fastjson.JSON;
import com.lww.sparrow.task.dal.JobInfoMapper;
import com.lww.sparrow.task.domain.entity.JobInfo;
import com.lww.sparrow.task.domain.util.OrikaBeanUtil;
import com.lww.sparrow.task.domain.bean.JobQueueBean;
import com.lww.sparrow.task.service.job.JobInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Component("jobScanService")
public class JobScanService {
    private static final Logger logger = LoggerFactory.getLogger(JobScanService.class);

    private ReentrantLock LOCK = new ReentrantLock();
    private Condition TIME_ARRIVED = LOCK.newCondition();

    @Resource
    private JobInfoMapper jobInfoMapper;
    @Resource
    private JobInfoService jobInfoService;
    @Resource
    private JobQueue jobQueue;
    @Resource
    private OrikaBeanUtil orikaBeanUtil;

    private AtomicBoolean isRunning = new AtomicBoolean(true);
    /**
     * 时间扫描范围 记录上次扫描时间 使用 startDate < X <= now()
     */
    private AtomicLong startDate = new AtomicLong(0);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 如果超过 2 秒那么线程就sleep
     */
    private long scanCanSleepTime = 2000;
    /**
     * 批量大小
     */
    private static final int BATCH_SIZE = 100;

    /**
     * 如果扫描没有任务那么sleep到 2028-08-01 00:00:00（1848672000） 当有新任务进来是会唤醒的
     */
    private long maxDate = 1848672000000L;
    /**
     * spring加载该bean时，自动执行该方法；
     * 目前是单线程版不考虑 多线程间重复扫描
     */
    @PostConstruct
    public void start() {
        new Thread(this::scanThreadRun).start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info(".................. jobScan stop ..................");
            isRunning.set(false);
            LOCK.lock();
            try {
                TIME_ARRIVED.signalAll();
            } finally {
                LOCK.unlock();
            }
        }));
    }
    private void scanThreadRun() {
        logger.info(".................. jobScan start ..................");
        while (isRunning.get()) {
            Date endDate = new Date();
            while (true) {
                List<JobInfo> jobInfos = jobInfoService.scanJobAndUpdateNextTime(new Date(startDate.get()), endDate, BATCH_SIZE);
                if (CollectionUtils.isEmpty(jobInfos)) {
                    break;
                }
                List<JobQueueBean> jobQueueBeans = orikaBeanUtil.convertList(jobInfos, JobQueueBean.class);
                Date scanDate = new Date();
                jobQueueBeans.forEach(jobQueueBean -> {
                    jobQueueBean.setScanDate(scanDate);
                    this.addJob(jobQueueBean, 0);
                });
                logger.info(".......... jobScan startDate:{} endDate:{} size:{}..........", DATE_FORMAT.format(startDate), DATE_FORMAT.format(endDate), jobInfos.size());
            }
            startDate.set(endDate.getTime());
            // 获取下次执行时间 如果太长则sleep
            threadWait();
        }
    }

    private void threadWait() {
        Date nextExecuteTime = jobInfoMapper.findNextExecuteTime(new Date(startDate.get()));
        if (nextExecuteTime == null || nextExecuteTime.before(new Date(startDate.get()))) {
            logger.error("jobScan no job need execute");
            nextExecuteTime = new Date(maxDate);
        }
        threadWait(nextExecuteTime);
    }

    private boolean threadWait(Date nextExecuteTime) {
        Throwable throwable = null;
        try {
            LOCK.lockInterruptibly();
            try {
                if ((nextExecuteTime.getTime() - startDate.get()) > scanCanSleepTime) {
                    TIME_ARRIVED.awaitUntil(nextExecuteTime);
                } else {
//                    TIME_ARRIVED.await(scanCanSleepTime, TimeUnit.MILLISECONDS);
                }
            } catch (Throwable e) {
                throwable = e;
            }
        } catch (Throwable e) {
            throwable = e;
        } finally {
            LOCK.unlock();
        }
        if (throwable != null) {
            logger.error("threadWait exception:", throwable);
            return false;
        }
        return true;
    }

    /**
     * 添加 job 到队列中
     * @param jobQueueBean
     * @return
     */
    private static Integer MAX_ADD_QUEUE_RETRY_COUNT = 5;
    private boolean addJob(JobQueueBean jobQueueBean, int count) {
        jobQueueBean.setAddqueueDate(new Date());
        boolean success = jobQueue.offer(jobQueueBean);
        if (!success ) {
            if (count < MAX_ADD_QUEUE_RETRY_COUNT) {
                return addJob(jobQueueBean, count++);
            } else {
                logger.error("jobQueueBean:{} retry {} times, still can`t add in queues", JSON.toJSONString(jobQueueBean), count);
            }
        }
        return success;
    }
    void wakeup() {
        LOCK.lock();
        try {
            TIME_ARRIVED.signal();
        } finally {
            LOCK.unlock();
        }
    }
}

package com.lww.sparrow.task.manager;

import com.alibaba.fastjson.JSON;
import com.lww.sparrow.task.dal.JobLogMapper;
import com.lww.sparrow.task.domain.bean.JobExecuteLogBean;
import com.lww.sparrow.task.domain.bean.JobQueueBean;
import com.lww.sparrow.task.domain.util.OrikaBeanUtil;
import com.lww.sparrow.task.manager.handler.HttpJobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class JobDispatcherService {

    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(8, 64,
                5L,TimeUnit.MINUTES, new LinkedBlockingQueue<>(1000), new NamedThreadFactory(false));

    private static final Logger LOGGER_DISPATCHER = LoggerFactory.getLogger("DISPATCHER");
    private AtomicBoolean isRunning = new AtomicBoolean(true);


    @Resource
    private JobQueue jobQueue;
    @Resource
    private OrikaBeanUtil orikaBeanUtil;
    @Resource
    private JobLogMapper jobLogMapper;

    @PostConstruct
    public void start() {
        new Thread(() -> dispatcherThreadRun()).start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER_DISPATCHER.info(".................. dispatcher stop ..................");
            isRunning.set(false);
        }));
    }

    /**
     * 由于是在队列中加锁，并且确保 poll 不为null 所以 isRunning 没啥用～就强制停止吧
     */
    private void dispatcherThreadRun() {
        LOGGER_DISPATCHER.info(".................. dispatcher start ..................");
        // 需要全部执行完才能退出
        while (isRunning.get() || jobQueue.size() > 0) {
            while (true) {
                JobQueueBean jobQueueBean = jobQueue.poll();
                if (jobQueueBean.getExecuteDate().after(new Date())) {
                    jobQueue.offer(jobQueueBean);
                    break;
                }
                JobExecuteLogBean jobExecuteLogBean = orikaBeanUtil.convert(jobQueueBean, JobExecuteLogBean.class);
                // 设置丢到线程池时间为当前时间
                jobExecuteLogBean.setExecuteDate(new Date());
                // 目前只有 http 请求 写死咯～
                LOGGER_DISPATCHER.info("submit job :{}", JSON.toJSONString(jobExecuteLogBean));
                threadPool.submit(new HttpJobHandler(jobExecuteLogBean, orikaBeanUtil, jobLogMapper));
            }
        }
        LOGGER_DISPATCHER.info(".................. dispatcher stop ..................");
    }

}
class NamedThreadFactory implements ThreadFactory {

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    private boolean isDamon;

    public NamedThreadFactory(boolean isDamon) {
        this.isDamon = isDamon;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName("jobHandler" + "-" + generateThreadId());
        thread.setDaemon(isDamon);
        return thread;
    }

    private static synchronized int generateThreadId() {
        return atomicInteger.getAndIncrement();
    }
}
package com.lww.sparrow.task.manager;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.lww.sparrow.task.domain.bean.*;

import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class JobQueue {
    private static final Logger logger = LoggerFactory.getLogger(JobQueue.class);

    private PriorityQueue<JobQueueBean> jobQueueBeans = new PriorityQueue<>((queueBean1, queueBean2) -> queueBean1.getExecuteDate().before(queueBean2.getExecuteDate()) ? -1 : queueBean1.getExecuteDate().after(queueBean2.getExecuteDate()) ? 1 : 0);

    private ReentrantLock LOCK = new ReentrantLock();
    private Condition CAN_GET = LOCK.newCondition();


    public static void main(String[] args) {
        JobQueue jobQueue = new JobQueue();
        JobQueueBean jobQueueBean1 = new JobQueueBean();
        jobQueueBean1.setExecuteDate(new Date(12000));
        jobQueueBean1.setJobName("test1");
        JobQueueBean jobQueueBean2 = new JobQueueBean();
        jobQueueBean2.setExecuteDate(new Date(10001));
        jobQueueBean2.setJobName("test2");
        JobQueueBean jobQueueBean3 = new JobQueueBean();
        jobQueueBean3.setExecuteDate(new Date(9099));
        jobQueueBean3.setJobName("test3");

        jobQueue.offer(jobQueueBean1);
        jobQueue.offer(jobQueueBean2);
        jobQueue.offer(jobQueueBean3);
        System.out.println(jobQueue.size());
        new Thread(() -> {
            while (true) {
                JobQueueBean jobQueueBean4 = jobQueue.poll();
                System.out.println("poll 1"+JSON.toJSONString(jobQueueBean4) + "size:" +jobQueue.size());
            }
        }).start();

        new Thread(() -> {
            int i = 100;
            while (i-- > 10) {
                JobQueueBean jobQueueBean4 = new JobQueueBean();
                jobQueueBean4.setExecuteDate(new Date(9099));
                jobQueueBean4.setJobName("test3");
                jobQueue.offer(jobQueueBean4);
                System.out.println(jobQueue.size());
            }
        }).start();

        new Thread(() -> {
            int i = 100;
            while (i-- > 10) {
                JobQueueBean jobQueueBean4 = new JobQueueBean();
                jobQueueBean4.setExecuteDate(new Date(9099));
                jobQueueBean4.setJobName("test3");
                jobQueue.offer(jobQueueBean4);
                System.out.println(jobQueue.size());
            }
        }).start();

        new Thread(() -> {
            while (true) {
                JobQueueBean jobQueueBean4 = jobQueue.poll();
                System.out.println("poll 2" + JSON.toJSONString(jobQueueBean4) + "size:" +jobQueue.size());
            }
        }).start();
//        jobQueueBean1 = jobQueue.poll();
//        jobQueueBean2 = jobQueue.poll();
//        jobQueueBean3 = jobQueue.poll();
        System.out.println(jobQueue.size());
    }

    /**
     * 返回队列头部的元素 如果队列为空，则返回null
     *
     * @return
     */
    public JobQueueBean peek() {
        LOCK.lock();
        JobQueueBean jobQueueBean = null;
        try {
            jobQueueBean = jobQueueBeans.peek();
            if (jobQueueBean == null) {
                CAN_GET.await();
                jobQueueBean = peek();
            }
        } catch (InterruptedException e) {
            logger.error("peek await exception:", e);
        } finally {
            LOCK.unlock();
        }
        return jobQueueBean;
    }

    /**
     * 移除并返问队列头部的元素  如果队列为空，则返回null
     *
     * @return
     */
    public JobQueueBean poll() {
        LOCK.lock();
        JobQueueBean jobQueueBean = null;
        try {
            jobQueueBean = jobQueueBeans.poll();
            if (jobQueueBean == null) {
                logger.info("poll i`m sleep ");
                CAN_GET.await();
                logger.info("poll i`m  signal");
                jobQueueBean = poll();
            }
        } catch (InterruptedException e) {
            logger.error("poll await exception:", e);
        } finally {
            LOCK.unlock();
        }
        return jobQueueBean;
    }

    /**
     * 添加一个元素并返回true       如果队列已满，则返回false
     *
     * @return
     */
    public  boolean offer(JobQueueBean jobQueueBean) {
        if (jobQueueBean == null) {
            return false;
        }
        LOCK.lock();
        boolean success = jobQueueBeans.offer(jobQueueBean);
        if (success) {
            CAN_GET.signal();
        }
        LOCK.unlock();
        return success;
    }

    /**
     * 增加 如果队列已满，则抛出一个IIIegaISlabEepeplian异常
     *
     * @return
     */
    public boolean offerAll(List<JobQueueBean> jobQueueBeans) {
        if (CollectionUtils.isEmpty(jobQueueBeans)) {
            return false;
        }
        jobQueueBeans.forEach(this::offer);
        return true;
    }

    /**
     * 添加一个元素并返回true       如果队列已满，则返回false
     *
     * @return
     */
    public int size() {
        return jobQueueBeans.size();
    }


}

package org.scy.common.manager;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * 定时器管理类
 * Created by shicy on 2017/9/16
 */
public class SchedulerManager {

    private Logger logger = LoggerFactory.getLogger(SchedulerManager.class);

    private Scheduler scheduler;
    private static SchedulerManager schedulerManager;

    /**
     * 私有构造方法，单例模式，请使用{@link #getInstance()}方法获取实例对象
     */
    private SchedulerManager() {
        try {
            // 初始化并启动定时器
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("定时器初始化失败：" + e.getMessage());
        }
    }

    /**
     * 获取定时器管理对象实例
     */
    public static SchedulerManager getInstance() {
        if (schedulerManager == null) {
            synchronized (SchedulerManager.class) {
                if (schedulerManager == null)
                    schedulerManager = new SchedulerManager();
            }
        }
        return schedulerManager;
    }

    /**
     * 新建一个 Cron 表达式触发器
     * @param cronExpression Cron 表达式
     * @return Cron 类型触发器
     */
    public static Trigger newCronTrigger(String cronExpression) {
        return newCronTrigger(cronExpression, false);
    }

    /**
     * 新建一个 Cron 表达式触发器
     * @param cronExpression Cron 表达式
     * @param startNow 是否立即执行
     * @return Cron 类型触发器
     */
    public static Trigger newCronTrigger(String cronExpression, boolean startNow) {
        TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger();
        triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression));
        if (startNow)
            triggerBuilder.startNow();
        return triggerBuilder.build();
    }

    /**
     * 添加定时任务，进入调度等待执行
     * @param jobCls 被添加的任务
     * @param trigger 任务触发器
     * @return 任务名称
     */
    public String addScheduleJob(Class<? extends Job> jobCls, Trigger trigger) {
        return addScheduleJob(jobCls, trigger, null);
    }

    /**
     * 添加定时任务，进入调度等待执行
     * @param jobCls 被添加的任务
     * @param data 数据集
     * @return 任务名称
     */
    public String addScheduleJob(Class<? extends Job> jobCls, Trigger trigger, Map<String, Object> data) {
        if (this.scheduler == null)
            return null;

        if (jobCls == null || trigger == null) {
            logger.error("定时任务或触发器不能为空");
        }
        else {
            String jobName = jobCls.getName() + "." + (new Date()).getTime();

            JobBuilder jobBuilder = JobBuilder.newJob(jobCls);
            jobBuilder.withIdentity(jobName, Scheduler.DEFAULT_GROUP);
            jobBuilder.setJobData(new JobDataMap(data));

            JobDetail jobDetail = jobBuilder.build();

            try {
                this.scheduler.scheduleJob(jobDetail, trigger);
            }
            catch (SchedulerException e) {
                logger.error("添加定时任务失败：" + e.getMessage());
            }
            return jobName;
        }

        return null;
    }

    /**
     * 自定义定时任务
     */
    public abstract static class MyJob implements Job {

        /**
         * 任务开始执行
         * @param jobExecutionContext 上下文
         * @throws JobExecutionException 执行异常
         */
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            this.executeJob(jobExecutionContext.getJobDetail().getJobDataMap());
        }

        /**
         * 定时任务执行方法，将在独立线程中运行
         * @param data 当前任务数据集
         * @throws JobExecutionException 异常信息
         */
        protected abstract void executeJob(JobDataMap data) throws JobExecutionException;

    }

    /**
     * 基于线程的任务，开启后在独立线程中进行
     */
    public abstract static class ThreadJob extends MyJob implements Runnable {

        private Logger logger = LoggerFactory.getLogger(ThreadJob.class);

        // 执行任务时的上下文对象
        protected JobExecutionContext context;

        // 当前执行线程对象
        private Thread currentThread;

        /**
         * 任务开始执行
         * @param jobExecutionContext 上下文
         * @throws JobExecutionException 执行异常
         */
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            this.context = jobExecutionContext;
            if (!this.isRunning()) {
                currentThread = new Thread(this);
                currentThread.start();
            }
        }

        /**
         * 在独立线程中运行
         */
        public final void run() {
            try {
                this.executeJob(context.getJobDetail().getJobDataMap());
            }
            catch (JobExecutionException e) {
                logger.error("", e);
            }
            currentThread = null;
        }

        /**
         * 判断当前任务是否正在执行
         */
        private boolean isRunning() {
            return currentThread != null && currentThread.isAlive();
        }

    }

}

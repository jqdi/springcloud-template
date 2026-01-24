package com.company.framework.threadpool;

import java.util.concurrent.*;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.task.TaskDecorator;

/**
 * 参考ThreadPoolTaskExecutor实现，利用taskDecorator可以传递任务信息（日志ID、上下文）
 */
@Slf4j(topic = "LOG_THREADPOOL")
public class TaskDecoratorThreadPoolExecutor extends ThreadPoolExecutor {
	private TaskDecorator taskDecorator;

	public TaskDecoratorThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
								   BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler, TaskDecorator taskDecorator) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		this.taskDecorator = taskDecorator;
	}

	public TaskDecoratorThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
								   BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler, TaskDecorator taskDecorator) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		this.taskDecorator = taskDecorator;
	}

	public TaskDecoratorThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
								   BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, TaskDecorator taskDecorator) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		this.taskDecorator = taskDecorator;
	}

	public TaskDecoratorThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
								   BlockingQueue<Runnable> workQueue, TaskDecorator taskDecorator) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		this.taskDecorator = taskDecorator;
	}

    public TaskDecoratorThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, @NotNull TimeUnit unit,
        @NotNull BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

	/**
	 * 保存任务开始执行的时间，当任务结束时，用任务结束时间减去开始时间计算任务执行时间
	 */
	private ThreadLocal<Long> timetl = new ThreadLocal<>();

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		timetl.set(System.currentTimeMillis());
		super.beforeExecute(t, r);
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		Long start = timetl.get();
		timetl.remove();
		long diff = System.currentTimeMillis() - start;
		// 统计任务耗时、初始线程数、正在执行的任务数量、 已完成任务数量、任务总数、队列里缓存的任务数量、池中存在的最大线程数
		log.info("duration:{} ms,poolSize:{},active:{},completedTaskCount:{},taskCount:{},queue:{},largestPoolSize:{}",
				diff, this.getPoolSize(), this.getActiveCount(), this.getCompletedTaskCount(), this.getTaskCount(),
				this.getQueue().size(), this.getLargestPoolSize());
	}

	@Override
	public void execute(Runnable command) {
		Runnable decorated = taskDecorator.decorate(command);
		super.execute(decorated);
	}
}

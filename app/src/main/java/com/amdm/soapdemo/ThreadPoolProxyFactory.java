package com.amdm.soapdemo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author luoshipeng
 * createDate：2019/3/20
 * className   ThreadPoolProxyFactory
 * Des：TODO
 */
public class ThreadPoolProxyFactory {
    private ThreadPoolExecutor mExecutor;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 2000;
    public static ThreadPoolProxyFactory newInstance() {

        return new ThreadPoolProxyFactory();
    }

    public void execute(Runnable task) {
        initThreadPoolExecutor();
        mExecutor.execute(task);
    }

    public Future<String> submit(Callable<String> task) {
        initThreadPoolExecutor();
        return mExecutor.submit(task);
    }

    private void initThreadPoolExecutor() {
        if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
                    TimeUnit unit = TimeUnit.MILLISECONDS;
                    BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<>();
                    ThreadFactory threadFactory = Executors.defaultThreadFactory();
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
                    mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, unit, workQueue,
                            threadFactory, handler);
                }
            }
        }
    }
}

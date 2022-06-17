package com.kuangheng.cloud.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 *
 * @author tiger
 * @date 2021-05-31 18:20:19
 */
public class ThreadPool {
    private static ExecutorService executor = new ThreadPoolExecutor(5, 5, 5 * 60, TimeUnit.SECONDS, new
            LinkedBlockingQueue<>());
    private static ThreadPool instance = new ThreadPool();

    private ThreadPool() {
    }

    public static ThreadPool getInstance() {
        return instance;
    }

    public void executeRunnable(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        executor.execute(runnable);
    }
}

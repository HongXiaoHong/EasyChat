package com.hong.utils;

import java.util.concurrent.*;

/**
 * 使用线程池执行线程
 */

public class ThreadPoolUtils {
    private final static ExecutorService pool = new ThreadPoolExecutor(5, 200,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    public static Future<?> submit(Runnable runnable) {
        return pool.submit(runnable);
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return pool.submit(callable);
    }
}

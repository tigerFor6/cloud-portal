package com.kuangheng.cloud.util;


import java.util.concurrent.Callable;

public class MyCallable implements Callable<Integer> {

    public Integer call() throws Exception {
        // 异步线程执行逻辑
        return 0;
    }
}

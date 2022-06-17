package com.kuangheng.cloud.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Service
public class ZingerThreadUtil {
    private static final Logger logger = LoggerFactory.getLogger(ZingerThreadUtil.class);

    public void execute(Callable<Integer> myCallable){
        // 往线程执行记录表中插入数据，状态记录为0，等待中
        FutureTask<Integer> future = new FutureTask<Integer>(myCallable);
        Thread thread = new Thread(future);
        thread.start();
        // 更改执行记录表中的状态为2，执行中
        try {
            int sum = future.get();
            boolean done = future.isDone();
            if (done){
                // 更改执行记录表中的状态为1，完成
            }else{
                // 更改执行记录表中的状态为2，执行中
            }
        } catch (Exception e) {
            // 更改执行记录表中的状态为3，异常
            logger.info(e.getMessage());
        }
    }
}

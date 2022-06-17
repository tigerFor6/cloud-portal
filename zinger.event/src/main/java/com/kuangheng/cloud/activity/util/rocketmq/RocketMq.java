package com.kuangheng.cloud.activity.util.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

public class RocketMq {

    public static void main(String[] args) {
        try {
            DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
            producer.setNamesrvAddr("219.141.235.67:48819");//MQ服务器地址
            producer.setVipChannelEnabled(false);
            producer.start();
            for (int i = 0; i < 2; i++) {
                Message msg = new Message("TopicTest", "TagA", ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = producer.send(msg);
                System.out.printf("%s%n", sendResult);
                System.out.println("--");
            }
            producer.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

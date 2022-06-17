package com.kuangheng.cloud.tag.util.rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class MQProducer {

    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("rmq-group");
        producer.setNamesrvAddr("219.141.235.67:22036");
        producer.setInstanceName("producer");
        producer.start();
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000); //每秒发送一次
                Message msg = new Message("itmayiedu-topic", // topic 主题名称
                        "TagA", // tag 临时值
                        ("itmayiedu-" + i).getBytes()// body 内容
                );
                SendResult sendResult = producer.send(msg, 5000);
                System.out.println(sendResult.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.shutdown();
    }

}

package com.kuangheng.cloud.service;

/**
 * kafka业务处理类
 */
public interface KafkaService {
    /**
     * 发送消息
     *
     * @param topic
     * @param data
     */
    void sendMessage(String topic, String data);

}

package com.kuangheng.cloud.service.impl;

import com.kuangheng.cloud.service.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.sql.DataSource;

@Service("kafkaService")
public class KafkaServiceImpl implements KafkaService {

    private Logger logger = LoggerFactory.getLogger(KafkaServiceImpl.class);

    private final KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 注入KafkaTemplate
     *
     * @param kafkaTemplate kafka模版类
     */
    @Autowired
    public KafkaServiceImpl(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "#{kafkaTopicName}", groupId = "#{topicGroupId}")
    public void processMessage(ConsumerRecord<Integer, String> record) {
        logger.info("kafka processMessage start");
        logger.info("processMessage, topic = {}, msg = {}", record.topic(), record.value());

        // do something ...

        logger.info("kafka processMessage end");
    }

    @Override
    public void sendMessage(String topic, String data) {
        logger.info("kafka sendMessage start");
        ListenableFuture<SendResult<Integer, String>> future = kafkaTemplate.send(topic, data);
        future.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                logger.error("kafka sendMessage error, ex = {}, topic = {}, data = {}", ex, topic, data);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                logger.info("kafka sendMessage success topic = {}, data = {}", topic, data);
            }
        });
        logger.info("kafka sendMessage end");
    }
}
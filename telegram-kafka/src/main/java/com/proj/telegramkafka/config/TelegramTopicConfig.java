package com.proj.telegramkafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TelegramTopicConfig {

    @Bean
    public NewTopic telegramTopic() {
        return TopicBuilder.name("telegram").build();
    }
}
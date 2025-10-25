// mq/RabbitMQConfig.java
package com.CodeExamner.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 评测请求队列
    @Bean
    public Queue judgeQueue() {
        return new Queue("judge.queue", true);
    }

    // 评测结果队列
    @Bean
    public Queue resultQueue() {
        return new Queue("result.queue", true);
    }

    // 评测交换机
    @Bean
    public DirectExchange judgeExchange() {
        return new DirectExchange("judge.exchange");
    }

    // 结果交换机
    @Bean
    public DirectExchange resultExchange() {
        return new DirectExchange("result.exchange");
    }

    // 绑定
    @Bean
    public Binding judgeBinding(Queue judgeQueue, DirectExchange judgeExchange) {
        return BindingBuilder.bind(judgeQueue).to(judgeExchange).with("judge.routing.key");
    }

    @Bean
    public Binding resultBinding(Queue resultQueue, DirectExchange resultExchange) {
        return BindingBuilder.bind(resultQueue).to(resultExchange).with("result.routing.key");
    }

    // JSON消息转换器
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
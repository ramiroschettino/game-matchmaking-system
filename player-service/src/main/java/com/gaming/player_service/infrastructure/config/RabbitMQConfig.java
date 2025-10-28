package com.gaming.player_service.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange
    public static final String GAME_EVENTS_EXCHANGE = "game.events";

    // Queues
    public static final String MATCH_FOUND_QUEUE = "match.found.queue";
    public static final String MATCH_COMPLETED_QUEUE = "match.completed.queue";

    // Routing Keys
    public static final String PLAYER_JOINED_QUEUE_KEY = "player.queue.joined";
    public static final String PLAYER_LEFT_QUEUE_KEY = "player.queue.left";
    public static final String MATCH_FOUND_KEY = "match.found";
    public static final String MATCH_COMPLETED_KEY = "match.completed";

    @Bean
    public TopicExchange gameEventsExchange() {
        return new TopicExchange(GAME_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue matchFoundQueue() {
        return new Queue(MATCH_FOUND_QUEUE, true);
    }

    @Bean
    public Queue matchCompletedQueue() {
        return new Queue(MATCH_COMPLETED_QUEUE, true);
    }

    @Bean
    public Binding matchFoundBinding(Queue matchFoundQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder
                .bind(matchFoundQueue)
                .to(gameEventsExchange)
                .with(MATCH_FOUND_KEY);
    }

    @Bean
    public Binding matchCompletedBinding(Queue matchCompletedQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder
                .bind(matchCompletedQueue)
                .to(gameEventsExchange)
                .with(MATCH_COMPLETED_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
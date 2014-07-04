package com.bole.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.ConnectionFactory;

@Configuration
public class QueueConfig {
	@Value("${rabbitmq.host}")
	private String hostname;

//	@Bean
	public ConnectionFactory getConnectionFactory() {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(hostname);
		return connectionFactory;
	}
}

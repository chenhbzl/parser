package com.bole.config;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Configuration
@Scope(value="singleton")
public class RabbitmqConfig {
	@Value("${rabbitmq.server}")
	private String serverip;
	
	@Value("${rabbitmq.server}")
	private String vhost;
	
	@Value("${rabbitmq.user}")
	private String user;
	
	@Value("${rabbitmq.password}")
	private String password;
	
	public Connection connection;
	
	
	@Bean(destroyMethod = "close")
	@Scope(value="singleton")
	public Connection getRabbitMqConnection() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(serverip);
		factory.setUsername(user);
		factory.setPassword(password);
	
		try {
			connection = factory.newConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
		
	}
	
	
}

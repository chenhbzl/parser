package com.bole.resumeparser.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bole.resumeparser.exception.ResumeParseException;
import com.bole.resumeparser.exception.document.UnSupportedResumeTypeException;
import com.bole.resumeparser.exception.html.ResumeMessageParserException;
import com.bole.resumeparser.exception.html.DocResumeParseException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;

public abstract class AbstractQueueListenerThread<T> extends Thread {
	private static Logger logger = LoggerFactory
			.getLogger(AbstractQueueListenerThread.class);
	
//	@Autowired
//	DocResumeParserThread docResumeParserThread;
//	@Autowired
//	protected ConnectionFactory connectionFactory;
	
	@Autowired
	Connection rabbitMqConnection;

	@Override
	public void run() {
		try {

			// 从rabbitmq中获取需要解析的简历
			System.out.println("Thread Started...");
			System.out.println("read message from rabbitmq...");
//			connectionFactory = new ConnectionFactory();
//			connectionFactory.setHost(DBHolder.rqhost);
//			Connection connection = rabbitMqConnection.newConnection();
			Channel channel = rabbitMqConnection.createChannel();

			String queueName = getQueueName();
			channel.queueDeclare(queueName, true, false, false, null);

			channel.basicQos(1);

			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, false, consumer);

			while (true) {
//				System.out.println("listening on rabbitmq...");
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				System.out.println(" [x] Received message from " + queueName
						+ ": " + "'" + message + "'");
				try {
					T obj = parseMessage(message);
					process(obj);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			}

		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public abstract T parseMessage(String message)
			throws ResumeMessageParserException;

	public abstract void process(T obj) throws ResumeParseException;
	public abstract void process() throws DocResumeParseException, UnSupportedResumeTypeException, Exception;
	public abstract String getQueueName();
}

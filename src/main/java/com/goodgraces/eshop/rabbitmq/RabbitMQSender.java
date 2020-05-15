package com.goodgraces.eshop.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSender {
	@Autowired
	private AmqpTemplate amqpTemplate;
	
	
	public void send(String queue,String message) {
		amqpTemplate.convertAndSend(queue,message);
	}
	
	
}

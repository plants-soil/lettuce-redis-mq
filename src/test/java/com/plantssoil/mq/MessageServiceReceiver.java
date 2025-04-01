package com.plantssoil.mq;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * In order to test message publish and receive in different process<br/>
 * This MessageServiceReceiver should be started MANUALLY as a java application
 * before MessageServiceSender started<br/>
 * Could start multiple times to simulate multiple processes to receive messages
 * concurrently<br/>
 * 
 * @auther duyong
 * @time 17:47:21
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MessageServiceReceiver {
	public static void main(String[] args) throws Exception {
		setUpBeforeClass();
		new MessageServiceReceiver().testReceiveMessage();
		tearDownAfterClass();
	}

	static IMessageServiceFactory factory;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		String redisHost = System.getenv("REDIS_HOST");
		String redisPassword = System.getenv("REDIS_PASSWORD");
		factory = IMessageServiceFactory.builder().host(redisHost).password(redisPassword).build();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@Test
	@Order(1)
	void testReceiveMessage() throws InterruptedException {
		String channel = "test01";
		// receive message
		factory.createMessageConsumer().channelName(channel).addMessageListener(new IMessageListener<String>() {
			@Override
			public void onMessage(String message, String consumerId) {
				System.out.println(message);
			}

		}).consume(String.class);
		Thread.sleep(10 * 1000);
	}
}
